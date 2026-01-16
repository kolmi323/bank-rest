package com.example.bankcards.controller;

import com.example.bankcards.dto.request.user.LoginUserRequest;
import com.example.bankcards.dto.request.user.RegisterUserRequest;
import com.example.bankcards.dto.response.JwtResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthController extends ApiController {
    AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody @Valid RegisterUserRequest request) {
        return authService.registerNewUser(request);
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        return authService.loginUser(loginUserRequest);
    }
}
