package com.example.bankcards.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegisterUserRequest {
    @NotBlank
    @JsonProperty("first_name")
    @Schema(
            description = "Имя пользователя",
            example = "Иван",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String firstName;

    @NotBlank
    @JsonProperty("last_name")
    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String lastName;

    @JsonProperty("middle_name")
    @Schema(
            description = "Отчество пользователя",
            example = "Иванович",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    String middleName;

    @Email
    @NotBlank
    @Schema(
            description = "Электронная почта (логин)",
            example = "ivan@mail.ru",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String email;

    @NotBlank
    @Schema(
            description = "Пароль для входа",
            example = "password123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    String password;
}
