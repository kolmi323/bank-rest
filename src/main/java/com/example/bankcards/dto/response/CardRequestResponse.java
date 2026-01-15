package com.example.bankcards.dto.response;

import com.example.bankcards.util.RequestStatus;
import com.example.bankcards.util.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardRequestResponse {
    Integer id;

    @JsonProperty("card_number")
    String cardNumber; // todo маскирование

    @JsonProperty("request_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    RequestType requestType;

    @JsonProperty("request_status")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    RequestStatus requestStatus;

    @JsonProperty("date_create")
    LocalDateTime dateCreate;
}
