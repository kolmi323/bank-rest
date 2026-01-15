package com.example.bankcards.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponse {
    Integer id;

    @JsonProperty("from_card_id")
    Integer fromCardId;

    @JsonProperty("to_card_id")
    Integer toCardId;

    BigDecimal amount;

    LocalDateTime date;
}
