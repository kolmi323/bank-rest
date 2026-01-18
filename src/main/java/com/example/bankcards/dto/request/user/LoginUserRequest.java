package com.example.bankcards.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на вход в систему")
public class LoginUserRequest {
    @Email
    @NotBlank
    @Schema(
            description = "Email пользователя",
            example = "ivan@mail.ru",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email;

    @NotBlank
    @Schema(
            description = "Пароль",
            example = "securePass123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String password;
}
