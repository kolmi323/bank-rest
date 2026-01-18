package com.example.bankcards.dto.response.request;

import com.example.bankcards.util.RequestStatus;
import com.example.bankcards.util.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Информация о заявке на изменение статуса карты")
public class CardRequestResponse {
    @Schema(description = "ID заявки", example = "101")
    Integer id;

    @JsonProperty("card_number")
    @Schema(description = "Номер карты (замаскированный)", example = "**** **** **** 1234")
    String cardNumber;

    @JsonProperty("request_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Тип запроса", example = "BLOCK")
    RequestType requestType;

    @JsonProperty("request_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(description = "Статус обработки заявки", example = "WAITING")
    RequestStatus requestStatus;

    @JsonProperty("date_create")
    @Schema(description = "Дата создания заявки", example = "2000-00-00T10:20:30")
    LocalDateTime dateCreate;
}
