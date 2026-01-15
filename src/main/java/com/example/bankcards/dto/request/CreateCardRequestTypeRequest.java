package com.example.bankcards.dto.request;

import com.example.bankcards.util.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCardRequestTypeRequest {
    @NotBlank
    @JsonProperty("card_id")
    @NotNull
    Integer cardId;
    
    @NotBlank
    @JsonProperty("request_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    RequestType requestType;
}
