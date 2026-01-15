package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateTransactionRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.dto.request.CreateCardRequestTypeRequest;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.service.CardRequestService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransactionService;
import com.example.bankcards.util.CardStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bank/card")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardController {
    CardService cardService;
    CardRequestService cardRequestService;
    TransactionService transactionService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(@RequestBody @Valid CreateCardRequest request) {
        return cardService.create(request);
    }

    @PostMapping("/block")
    @ResponseStatus(HttpStatus.CREATED)
    public CardRequestResponse createCardRequest(@RequestBody @Valid CreateCardRequestTypeRequest request) {
        return cardRequestService.createRequestForChangeStatusCard(request, CurrentUserMock.id);
    }

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody @Valid CreateTransactionRequest request) {
        return transactionService.create(request, CurrentUserMock.id);
    }

    @GetMapping("/view")
    public Page<CardResponse> getCards(@RequestParam(required = false, defaultValue = "0") int page,
                                       @RequestParam(required = false, defaultValue = "10") int size) {
        return cardService.getPageByUserId(CurrentUserMock.id, page, size);
    }

    @GetMapping("/filter")
    public Page<CardResponse> getCardsByStatus(@RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size,
                                               @RequestParam(required = false, defaultValue = "ACTIVE") CardStatus status) {
        return cardService.getPageByUserIdAndStatus(CurrentUserMock.id, status, page, size);
    }

    // todo подумать над тем, что написать отдельные response
    @GetMapping(value = "/balance")
    public BigDecimal getBalanceCard(@RequestParam int id) {
        return cardService.getCardBalanceByIdAndUserId(id, CurrentUserMock.id);
    }
}
