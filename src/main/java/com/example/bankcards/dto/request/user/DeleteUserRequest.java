package com.example.bankcards.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на удаление пользователя")
public class DeleteUserRequest {
    @NotNull
    @Min(1)
    @Schema(
            description = "ID пользователя для удаления",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer id;
}
