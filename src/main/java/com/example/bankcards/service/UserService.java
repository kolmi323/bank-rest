package com.example.bankcards.service;

import com.example.bankcards.dto.request.user.ChangeRoleRequest;
import com.example.bankcards.dto.request.user.DeleteUserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.converter.UserEntityToUserResponseConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserService {
    UserRepository userRepository;
    UserEntityToUserResponseConverter converter;

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь %s не найден", email)));
    }

    public UserResponse grantRole(int adminId, ChangeRoleRequest request) {
        validPossibilityChange(adminId, request.getUserId());
        UserEntity user = handleUserEntityById(request.getUserId());
        if (user.getRoles().add(UserRole.ADMIN)) {
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Права уже выданы");
        }
        return converter.convert(user);
    }

    public UserResponse revokeRole(int adminId, ChangeRoleRequest request) {
        validPossibilityChange(adminId, request.getUserId());
        UserEntity user = handleUserEntityById(request.getUserId());
        if (user.getRoles().remove(UserRole.ADMIN)) {
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Пользователь не имеет роль админа");
        }
        return converter.convert(user);
    }

    public void deleteUser(int adminId, DeleteUserRequest request) {
        validPossibilityChange(adminId, request.getId());
        UserEntity user = handleUserEntityById(request.getId());
        if (user.getRoles().contains(UserRole.ADMIN)) {
            throw new IllegalArgumentException("Удаление админа невозможно");
        } else {
            userRepository.delete(user);
        }
    }

    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(converter::convert);
    }

    private UserEntity handleUserEntityById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
    }

    private void validPossibilityChange(int requestUserId, int changeUserId) {
        if (requestUserId == changeUserId) {
            throw new IllegalArgumentException("Запрос на изменение своих данных невозможен");
        }
    }
}
