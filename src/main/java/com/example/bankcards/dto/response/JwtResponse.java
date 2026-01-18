package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "JWT токен для авторизации")
public class JwtResponse {
    @Schema(description = "Токен доступа (Bearer)", example = "eyJhbGciOiJIUzI1NiJ9...")
    String token;
}
