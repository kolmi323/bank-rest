package com.example.bankcards.dto.request.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на создание перевода средств")
public class CreateTransactionRequest {
    @JsonProperty("from_card_id")
    @NotNull
    @Min(1)
    @Schema(
            description = "ID карты списания",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer fromCardId;

    @JsonProperty("to_card_id")
    @NotNull
    @Min(1)
    @Schema(
            description = "ID карты зачисления",
            example = "20",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer toCardId;

    @Digits(integer = 20, fraction = 2)
    @Positive
    @NotNull
    @Schema(
            description = "Сумма перевода",
            example = "500.50",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    BigDecimal amount;
}
