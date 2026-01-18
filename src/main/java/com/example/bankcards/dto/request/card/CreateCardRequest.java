package com.example.bankcards.dto.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на создание новой карты")
public class CreateCardRequest {
    @JsonProperty("user_id")
    @NotNull
    @Min(1)
    @Schema(
            description = "ID владельца карты",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer userId;

    @JsonProperty("valid_period")
    @NotNull
    @Future
    @Schema(
            description = "Срок действия карты (должен быть в будущем)",
            example = "2030-12-31",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    LocalDate validPeriod;

    @NotNull
    @Digits(integer = 20, fraction = 2)
    @Positive
    @Schema(
            description = "Начальный баланс карты",
            example = "1000.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    BigDecimal balance;
}
