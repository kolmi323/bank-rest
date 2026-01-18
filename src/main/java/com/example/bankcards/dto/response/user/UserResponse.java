package com.example.bankcards.dto.response.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о пользователе")
public class UserResponse {
    @Schema(description = "ID пользователя", example = "1")
    int id;

    @JsonProperty("full_name")
    @Schema(description = "Полное имя", example = "Иван Иванов")
    String fullName;

    @Schema(description = "Электронная почта", example = "ivan@mail.ru")
    String email;
}
