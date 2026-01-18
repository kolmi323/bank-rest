package com.example.bankcards.dto.request.user;

import com.example.bankcards.util.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на изменение роли пользователя")
public class ChangeRoleRequest {
    @NotNull
    @JsonProperty("user_id")
    @Min(1)
    @Schema(
            description = "ID пользователя",
            example = "5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer userId;

    @NotNull
    @JsonProperty("role")
    @Schema(
            description = "Роль (ADMIN или USER)",
            example = "ADMIN",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    UserRole userRole;
}
