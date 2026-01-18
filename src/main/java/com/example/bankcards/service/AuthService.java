package com.example.bankcards.service;

import com.example.bankcards.dto.request.user.LoginUserRequest;
import com.example.bankcards.dto.request.user.RegisterUserRequest;
import com.example.bankcards.dto.response.JwtResponse;
import com.example.bankcards.dto.response.user.UserResponse;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.JwtTokenUtils;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.converter.UserEntityToUserResponseConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthService {
    UserRepository userRepository;
    CustomUserDetailsService customUserDetailsService;
    UserEntityToUserResponseConverter converter;
    JwtTokenUtils jwtTokenUtils;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;

    public UserResponse registerNewUser(RegisterUserRequest request) {
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(UserRole.USER);
        return converter.convert(userRepository.save(user));
    }

    public JwtResponse loginUser(LoginUserRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException(e.getMessage(), e.getCause());
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        return new JwtResponse(jwtTokenUtils.generateToken(userDetails.getUsername()));
    }
}
