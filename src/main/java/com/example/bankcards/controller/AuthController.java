package com.example.bankcards.controller;

import com.example.bankcards.dto.request.user.LoginUserRequest;
import com.example.bankcards.dto.request.user.RegisterUserRequest;
import com.example.bankcards.dto.response.ErrorResponse;
import com.example.bankcards.dto.response.JwtResponse;
import com.example.bankcards.dto.response.user.UserResponse;
import com.example.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Auth API",
        description = "Аутентификация/авторизация и регистрация пользователя"
)
@RestController
@RequestMapping("/bank")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;

    @Operation(summary = "Регистрация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан успешно",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Пользователь с таким email уже есть",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody @Valid RegisterUserRequest request) {
        return authService.registerNewUser(request);
    }

    @Operation(summary = "Аутентификация/авторизация")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "пользователь успешно вошёл",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "неверная почта или пароль",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public JwtResponse login(
            @RequestBody @Valid LoginUserRequest loginUserRequest
    ) {
        return authService.loginUser(loginUserRequest);
    }
}
