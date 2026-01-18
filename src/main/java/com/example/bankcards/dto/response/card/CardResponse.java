package com.example.bankcards.dto.response.card;

import com.example.bankcards.util.CardStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о карте")
public class CardResponse {
    @Schema(description = "ID карты", example = "15")
    Integer id;

    @JsonProperty("user_id")
    @Schema(description = "ID владельца", example = "5")
    Integer userId;

    @Schema(description = "Номер карты (замаскированный)", example = "**** **** **** 1234")
    String number;

    @JsonProperty("valid_period")
    @Schema(description = "Срок действия", example = "2028-12-31")
    LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Статус карты", example = "ACTIVE")
    CardStatus status;

    @Schema(description = "Баланс", example = "1000.00")
    BigDecimal balance;
}
