package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Стандартный ответ при ошибке")
public class ErrorResponse {
    @Schema(description = "Сообщение об ошибке", example = "Недостаточно средств на карте")
    String message;

    @Schema(description = "Время возникновения ошибки", example = "2000-00-00T01:02:30.000+03:00")
    Date timestamp;
}
