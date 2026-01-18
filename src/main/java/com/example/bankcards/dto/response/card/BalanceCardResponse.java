package com.example.bankcards.dto.response.card;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Ответ с балансом карты")
public class BalanceCardResponse {
    @Schema(description = "Текущий баланс", example = "5000.50")
    BigDecimal balance;
}
