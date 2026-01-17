package com.example.bankcards.service;

import com.example.bankcards.dto.request.user.ChangeRoleRequest;
import com.example.bankcards.dto.request.user.DeleteUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.converter.UserEntityToUserResponseConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks private UserService subj;

    @Mock private UserRepository userRepository;
    @Mock private UserEntityToUserResponseConverter converter;

    private UserEntity createFullUserEntity(int id, String email, Set<UserRole> roles) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(email);
        user.setPassword("encoded_password_123");
        user.setRoles(new HashSet<>(roles));
        return user;
    }

    @Test
    void findByEmail_returnUserEntity_whenUserExists() {
        String email = "test@example.com";

        UserEntity userEntity = createFullUserEntity(1, email, Set.of(UserRole.USER));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        UserEntity result = subj.findByEmail(email);

        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getEmail(), result.getEmail());
        assertEquals(userEntity.getFirstName(), result.getFirstName());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_throwNotFoundException_whenUserDoesNotExist() {
        String email = "test@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subj.findByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void grantRole_returnUserResponse_whenCalledWithValidArguments() {
        int adminId = 1;
        int targetUserId = 2;

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(targetUserId);
        request.setUserRole(UserRole.ADMIN);
        UserEntity targetUser = createFullUserEntity(
                targetUserId, "test@example.com", Collections.singleton(UserRole.USER)
        );
        UserResponse expectedResponse = new UserResponse(
                targetUserId,
                "John Doe",
                "test@example.com"
        );

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(converter.convert(targetUser)).thenReturn(expectedResponse);

        UserResponse result = subj.grantRole(adminId, request);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertTrue(targetUser.getRoles().contains(UserRole.ADMIN));
        verify(userRepository, times(1)).save(targetUser);
        verify(converter, times(1)).convert(targetUser);
    }

    @Test
    void grantRole_throwBadRequestException_whenAdminTriesToChangeSelf() {
        int adminId = 1;

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(adminId);
        request.setUserRole(UserRole.ADMIN);

        assertThrows(BadRequestException.class, () -> subj.grantRole(adminId, request));
        verifyNoInteractions(userRepository);
    }

    @Test
    void grantRole_throwBadRequestException_whenRoleAlreadyGranted() {
        int adminId = 1;
        int targetUserId = 2;

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(targetUserId);
        request.setUserRole(UserRole.ADMIN);
        UserEntity targetUser = createFullUserEntity(targetUserId, "test@example.com", Set.of(UserRole.USER, UserRole.ADMIN));

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        assertThrows(BadRequestException.class, () -> subj.grantRole(adminId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void grantRole_throwNotFoundException_whenUserNotFound() {
        int adminId = 1;
        int targetUserId = 3;

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(targetUserId);
        request.setUserRole(UserRole.ADMIN);

        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subj.grantRole(adminId, request));
    }

    @Test
    void revokeRole_returnUserResponse_whenCalledWithValidArguments() {
        int adminId = 1;
        int targetUserId = 2;

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(targetUserId);
        request.setUserRole(UserRole.ADMIN);
        UserEntity targetUser = createFullUserEntity(targetUserId, "test@example.com", Set.of(UserRole.USER, UserRole.ADMIN));
        UserResponse expectedResponse = new UserResponse(
                targetUserId,
                "John Doe",
                "test@example.com"
        );

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        when(converter.convert(targetUser)).thenReturn(expectedResponse);

        UserResponse result = subj.revokeRole(adminId, request);

        assertNotNull(result);
        assertFalse(targetUser.getRoles().contains(UserRole.ADMIN));
        verify(userRepository, times(1)).save(targetUser);
    }

    @Test
    void revokeRole_throwBadRequestException_whenUserHasNoAdminRole() {
        int adminId = 1;
        int targetUserId = 2;

        ChangeRoleRequest request = new ChangeRoleRequest();
        request.setUserId(targetUserId);
        UserEntity targetUser = createFullUserEntity(targetUserId, "test@example.com", Set.of(UserRole.USER));

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        assertThrows(BadRequestException.class, () -> subj.revokeRole(adminId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_success_whenUserIsNotAdmin() {
        int adminId = 1;
        int targetUserId = 2;

        DeleteUserRequest request = new DeleteUserRequest();
        request.setId(targetUserId);
        UserEntity targetUser = createFullUserEntity(targetUserId, "test@example.com", Set.of(UserRole.USER));

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        subj.deleteUser(adminId, request);

        verify(userRepository, times(1)).delete(targetUser);
    }

    @Test
    void deleteUser_throwBadRequestException_whenTargetIsAdmin() {
        int adminId = 1;
        int targetUserId = 2;

        DeleteUserRequest request = new DeleteUserRequest();
        request.setId(targetUserId);
        UserEntity targetUser = createFullUserEntity(targetUserId, "test@example.com", Set.of(UserRole.ADMIN));

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        assertThrows(BadRequestException.class, () -> subj.deleteUser(adminId, request));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void getAllUsers_returnPageOfUserResponse_whenCalledWithValidArguments() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        UserEntity userEntity = createFullUserEntity(5, "test@example.com", Set.of(UserRole.USER));
        UserResponse userResponse = new UserResponse(5, "John Doe", "test@example.com");
        Page<UserEntity> entityPage = new PageImpl<>(List.of(userEntity));

        when(userRepository.findAll(pageable)).thenReturn(entityPage);
        when(converter.convert(userEntity)).thenReturn(userResponse);

        Page<UserResponse> result = subj.getAllUsers(page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userResponse.getId(), result.getContent().get(0).getId());
        verify(userRepository, times(1)).findAll(pageable);
        verify(converter, times(1)).convert(userEntity);
    }
}