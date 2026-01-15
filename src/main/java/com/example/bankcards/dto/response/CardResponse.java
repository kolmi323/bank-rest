package com.example.bankcards.dto.response;

import com.example.bankcards.util.CardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardResponse {
    Integer id;

    @JsonProperty("user_id")
    Integer userId;

    String number; // todo маскирование

    @JsonProperty("valid_period")
    LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    CardStatus status;

    BigDecimal balance;
}
