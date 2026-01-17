package com.example.bankcards.service;

import com.example.bankcards.dto.request.user.LoginUserRequest;
import com.example.bankcards.dto.request.user.RegisterUserRequest;
import com.example.bankcards.dto.response.JwtResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CustomGrantedAuthority;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.JwtTokenUtils;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.converter.UserEntityToUserResponseConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @InjectMocks private AuthService subj;

    @Mock private UserRepository userRepository;
    @Mock private CustomUserDetailsService customUserDetailsService;
    @Mock private UserEntityToUserResponseConverter converter;
    @Mock private JwtTokenUtils jwtTokenUtils;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PasswordEncoder passwordEncoder;

    @Test
    void registerNewUser_returnUserResponse_whenRequestIsValid() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(request.getFirstName());
        userEntity.setLastName(request.getLastName());
        userEntity.setEmail(request.getEmail());
        userEntity.setPassword("encodedPassword");

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1);
        savedEntity.setFirstName(request.getFirstName());
        savedEntity.setLastName(request.getLastName());
        savedEntity.setEmail(request.getEmail());
        savedEntity.setPassword("encodedPassword");

        UserResponse expectedResponse = new UserResponse(1, "John Doe", request.getEmail());

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(userEntity)).thenReturn(savedEntity);
        when(converter.convert(savedEntity)).thenReturn(expectedResponse);

        UserResponse result = subj.registerNewUser(request);

        assertNotNull(result);
        assertEquals(expectedResponse.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void loginUser_returnJwtResponse_whenCredentialsAreCorrect() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        String expectedToken = "token";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        UserDetails userDetails = new CustomUserDetails(
                1,
                request.getEmail(),
                request.getPassword(),
                Set.of(UserRole.USER).stream()
                        .map(CustomGrantedAuthority::new)
                        .toList()
        );

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(null);
        when(customUserDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(request.getEmail())).thenReturn(expectedToken);

        JwtResponse result = subj.loginUser(request);

        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());
        verify(authenticationManager).authenticate(authenticationToken);
        verify(customUserDetailsService, times(1)).loadUserByUsername(request.getEmail());
    }

    @Test
    void loginUser_throwBadCredentialsException_whenAuthenticationFails() {
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrong_password");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () -> subj.loginUser(request));
        verify(customUserDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtTokenUtils, never()).generateToken(anyString());
    }
}