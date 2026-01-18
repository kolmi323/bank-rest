package com.example.bankcards.dto.request.req;

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
@Schema(description = "Запрос на обработку заявки (принять/отклонить)")
public class ChangeCardReqRequest {
    @JsonProperty("request_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull
    @Min(1)
    @Schema(
            description = "ID заявки",
            example = "100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    Integer requestId;
}
