package com.example.bankcards.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCardRequest {
    @JsonProperty("user_id")
    @NotNull
    Integer userId;

    @JsonProperty("valid_period")
    @NotNull
    @Future
    LocalDate validPeriod;

    @NotNull
    @Digits(integer = 20, fraction = 2)
    @Positive
    BigDecimal balance;
}
