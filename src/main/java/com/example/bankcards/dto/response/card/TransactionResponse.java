package com.example.bankcards.dto.response.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о проведенной транзакции")
public class TransactionResponse {
    @Schema(description = "ID транзакции", example = "505")
    Integer id;

    @JsonProperty("from_card_id")
    @Schema(description = "ID карты списания", example = "10")
    Integer fromCardId;

    @JsonProperty("to_card_id")
    @Schema(description = "ID карты зачисления", example = "20")
    Integer toCardId;

    @Schema(description = "Сумма перевода", example = "150.00")
    BigDecimal amount;

    @Schema(description = "Дата и время транзакции", example = "2000-00-00T10:20:30")
    LocalDateTime date;
}
