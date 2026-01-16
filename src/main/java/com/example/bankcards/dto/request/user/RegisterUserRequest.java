package com.example.bankcards.dto.request.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterUserRequest {
    @NotBlank
    @JsonProperty("first_name")
    String firstName;

    @NotBlank
    @JsonProperty("last_name")
    String lastName;

    @JsonProperty("middle_name")
    String middleName;

    @Email
    @NotBlank
    String email;

    @NotBlank
    String password;
}
