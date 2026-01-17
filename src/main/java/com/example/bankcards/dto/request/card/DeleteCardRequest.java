package com.example.bankcards.dto.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteCardRequest {
    @JsonProperty("user_id")
    @NotNull
    @Min(1)
    Integer userId;

    @JsonProperty("card_id")
    @NotNull
    @Min(1)
    Integer cardId;
}
