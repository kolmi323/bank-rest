package com.example.bankcards.dto.request.req;

import com.example.bankcards.util.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Запрос на создание заявки по изменению статуса карты")
public class CreateCardReqRequest {
    @JsonProperty("card_id")
    @NotNull
    @Min(1)
    @Schema(
            description = "ID карты",
            example = "15",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer cardId;

    @JsonProperty("request_type")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    @Schema(
            description = "Тип запроса (BLOCK или UNBLOCK)",
            example = "BLOCK",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    RequestType requestType;
}
