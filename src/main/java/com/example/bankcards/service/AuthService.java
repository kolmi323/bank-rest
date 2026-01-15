package com.example.bankcards.service;

import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.dto.request.RegisterUserRequest;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.UserRole;
import com.example.bankcards.util.converter.UserEntityToUserResponseConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthService {
    UserRepository userRepository;
    UserEntityToUserResponseConverter converter;

    public UserResponse register(RegisterUserRequest request) {
        UserEntity user = new UserEntity();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.getRoles().add(UserRole.USER);
        return converter.convert(userRepository.save(user));
    }
}
