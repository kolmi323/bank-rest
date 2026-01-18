package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.request.card.CreateCardRequest;
import com.example.bankcards.dto.request.card.DeleteCardRequest;
import com.example.bankcards.dto.response.card.CardResponse;
import com.example.bankcards.dto.response.ErrorResponse;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Admin Card API",
        description = "Административное управление картами: создание, удаление и просмотр"
)
@RestController
@RequestMapping("/bank/admin/card")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminCardController {
    CardService cardService;

    @Operation(summary = "Создание новой карты для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Карта успешно создана",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(
            @RequestBody @Valid CreateCardRequest request
    ) {
        return cardService.create(request);
    }

    @Operation(summary = "Удаление карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удаление успешно выполнено"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка удаления (например, удалилось более одной записи)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/delete")
    public void deleteCard(
            @RequestBody @Valid DeleteCardRequest request
    ) {
        cardService.deleteByIdAndUserId(request.getCardId(), request.getUserId());
    }

    @Operation(summary = "Просмотр всех карт (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт получен успешно")
    })
    @GetMapping("/view")
    public Page<CardResponse> viewCards(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return cardService.getPage(page, size);
    }

    @Operation(summary = "Фильтрация карт по статусу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен успешно")
    })
    @GetMapping("/filter/status")
    public Page<CardResponse> filterCardsByStatus(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Статус карты (ACTIVE, BLOCKED и т.д.)")
            @RequestParam(required = false, defaultValue = "ACTIVE") CardStatus status
    ) {
        return cardService.getPageByStatus(status, page, size);
    }

    @Operation(summary = "Фильтрация карт по ID владельца")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен успешно")
    })
    @GetMapping("/filter/user")
    public Page<CardResponse> filterCardsByStatus(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "ID пользователя")
            @Min(1) @RequestParam(name = "user_id") int userId
    ) {
        return cardService.getPageByUserId(userId, page, size);
    }
}
