package com.example.bankcards.controller.user;

import com.example.bankcards.dto.request.req.CreateTransactionRequest;
import com.example.bankcards.dto.request.req.CreateCardReqRequest;
import com.example.bankcards.dto.response.*;
import com.example.bankcards.dto.response.card.BalanceCardResponse;
import com.example.bankcards.dto.response.card.TransactionResponse;
import com.example.bankcards.dto.response.request.CardRequestResponse;
import com.example.bankcards.dto.response.card.CardResponse;
import com.example.bankcards.security.CustomUserDetails;
import com.example.bankcards.service.CardRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User Card API",
        description = "Операции пользователя: управление картами, просмотр баланса и переводы"
)
@RestController
@RequestMapping("/bank/user/card")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserCardController {
    CardService cardService;
    CardRequestService cardRequestService;
    TransactionService transactionService;

    @Operation(summary = "Создание заявки на изменение статуса карты (Блокировка/Разблокировка)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заявка успешно создана",
                    content = @Content(schema = @Schema(implementation = CardRequestResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (например, попытка разблокировать активную карту)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/status")
    @ResponseStatus(HttpStatus.CREATED)
    public CardRequestResponse createCardRequest(
            @RequestBody @Valid CreateCardReqRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return cardRequestService.createRequestForChangeStatusCard(request, userDetails.getId());
    }

    @Operation(summary = "Создание перевода между своими картами")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Транзакция успешно проведена",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации (одинаковые ID карт)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта списания или зачисления не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @RequestBody @Valid CreateTransactionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return transactionService.create(request, userDetails.getId());
    }

    @Operation(summary = "Получение списка карт пользователя (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список карт получен успешно")
    })
    @GetMapping("/view")
    public Page<CardResponse> getCards(
            @Parameter(description = "Номер страницы (начиная с 0)")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов на странице")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,

            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return cardService.getPageByUserId(userDetails.getId(), page, size);
    }

    @Operation(summary = "Фильтрация карт пользователя по статусу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отфильтрованный список карт получен успешно")
    })
    @GetMapping("/filter")
    public Page<CardResponse> getCardsByStatus(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Статус карты (ACTIVE, BLOCKED и т.д.)")
            @RequestParam(required = false, defaultValue = "ACTIVE") CardStatus status,

            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return cardService.getPageByUserIdAndStatus(userDetails.getId(), status, page, size);
    }

    @Operation(summary = "Получение баланса конкретной карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс получен успешно",
                    content = @Content(schema = @Schema(implementation = BalanceCardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Карта не найдена или не принадлежит пользователю",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/balance")
    public BalanceCardResponse getBalanceCard(
            @Parameter(description = "ID карты")
            @Min(1) @RequestParam int id,

            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return cardService.getBalanceByIdAndUserId(id, userDetails.getId());
    }
}
