package com.example.bankcards.dto.request.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на удаление карты")
public class DeleteCardRequest {
    @JsonProperty("user_id")
    @NotNull
    @Min(1)
    @Schema(
            description = "ID владельца карты",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer userId;

    @JsonProperty("card_id")
    @NotNull
    @Min(1)
    @Schema(
            description = "ID удаляемой карты",
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer cardId;
}
