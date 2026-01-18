package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.request.req.ChangeCardReqRequest;
import com.example.bankcards.dto.response.request.CardRequestResponse;
import com.example.bankcards.dto.response.ErrorResponse;
import com.example.bankcards.service.CardRequestService;
import com.example.bankcards.util.RequestStatus;
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
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Admin Card Request API",
        description = "Обработка запросов на изменение статуса карт (блокировка/разблокировка)"
)
@RestController
@RequestMapping("/bank/admin/request")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminRequestCardController {
    CardRequestService cardRequestService;

    @Operation(summary = "Одобрение запроса (Блокировка или Разблокировка карты)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявление одобрено, статус карты изменен",
                    content = @Content(schema = @Schema(implementation = CardRequestResponse.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (заявка на разблокировку активированной карты)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Запрос не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/accept")
    public CardRequestResponse acceptRequest(
            @RequestBody @Valid ChangeCardReqRequest request
    ) {
        return cardRequestService.acceptRequest(request.getRequestId());
    }

    @Operation(summary = "Отклонение запроса")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заявление отклонено успешно",
                    content = @Content(schema = @Schema(implementation = CardRequestResponse.class))),
            @ApiResponse(responseCode = "404", description = "Запрос не найден",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reject")
    public CardRequestResponse rejectRequest(
            @RequestBody @Valid ChangeCardReqRequest request
    ) {
        return cardRequestService.rejectRequest(request.getRequestId());
    }

    @Operation(summary = "Просмотр всех запросов (с пагинацией)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список запросов получен успешно")
    })
    @GetMapping("/view")
    public Page<CardRequestResponse> viewRequests(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return cardRequestService.getPage(page, size);
    }

    @Operation(summary = "Фильтрация запросов по статусу заявки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен успешно")
    })
    @GetMapping("/filter/status")
    public Page<CardRequestResponse> filterRequestsByStatus(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Статус заявки (WAITING, ACCEPTED, REJECTED)")
            @RequestParam(required = false, defaultValue = "WAITING") RequestStatus status
    ) {
        return cardRequestService.getPageByRequestStatus(status, page, size);
    }

    @Operation(summary = "Фильтрация запросов по ID карты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен успешно")
    })
    @GetMapping("/filter/card")
    public Page<CardRequestResponse> filterRequestsByCardId(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "ID карты")
            @RequestParam(required = false, defaultValue = "1", name = "card_id") int cardId
    ) {
        return cardRequestService.getPageByCardId(cardId, page, size);
    }

    @Operation(summary = "Фильтрация запросов по ID карты и статусу одновременно")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список получен успешно")
    })
    @GetMapping("/filter/main")
    public Page<CardRequestResponse> filterRequestsByCardIdAndStatus(
            @Parameter(description = "Номер страницы")
            @Min(0) @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Количество элементов")
            @Min(1) @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "ID карты")
            @Min(1) @RequestParam(name = "card_id") int cardId,

            @Parameter(description = "Статус заявки")
            @RequestParam(required = false, defaultValue = "WAITING") RequestStatus status
    ) {
        return cardRequestService.getPageByCardIdAndRequestStatus(cardId, status, page, size);
    }
}
