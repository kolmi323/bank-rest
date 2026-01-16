package com.example.bankcards.dto.request.request;

import com.example.bankcards.util.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCardReqRequest {
    @JsonProperty("card_id")
    @NotNull
    Integer cardId;

    @JsonProperty("request_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    RequestType requestType;
}
