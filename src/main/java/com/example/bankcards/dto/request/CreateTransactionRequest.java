package com.example.bankcards.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTransactionRequest {
    @JsonProperty("from_card_id")
    Integer fromCardId;

    @JsonProperty("to_card_id")
    Integer toCardId;

    @Digits(integer = 20, fraction = 2)
    @Positive
    @NotNull
    BigDecimal amount;
}
