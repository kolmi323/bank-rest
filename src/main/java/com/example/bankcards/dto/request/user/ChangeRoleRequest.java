package com.example.bankcards.dto.request.user;

import com.example.bankcards.util.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangeRoleRequest {
    @NotNull
    @JsonProperty("user_id")
    Integer userId;

    @NotNull
    @JsonProperty("role")
    UserRole userRole;
}
