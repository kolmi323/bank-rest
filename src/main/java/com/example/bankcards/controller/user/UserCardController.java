package com.example.bankcards.controller.user;

import com.example.bankcards.dto.request.request.CreateCardReqRequest;
import com.example.bankcards.dto.request.card.CreateTransactionRequest;
import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.security.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bank/user/card")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserCardController {
    CardService cardService;
    CardRequestService cardRequestService;
    TransactionService transactionService;

    @PostMapping("/status")
    @ResponseStatus(HttpStatus.CREATED)
    public CardRequestResponse createCardRequest(
            @RequestBody @Valid CreateCardReqRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return cardRequestService.createRequestForChangeStatusCard(request, userDetails.getId());
    }

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @RequestBody @Valid CreateTransactionRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return transactionService.create(request, userDetails.getId());
    }

    @GetMapping("/view")
    public Page<CardResponse> getCards(@RequestParam(required = false, defaultValue = "0") int page,
                                       @RequestParam(required = false, defaultValue = "10") int size,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        return cardService.getPageByUserId(userDetails.getId(), page, size);
    }

    @GetMapping("/filter")
    public Page<CardResponse> getCardsByStatus(@RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size,
                                               @RequestParam(required = false, defaultValue = "ACTIVE") CardStatus status,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        return cardService.getPageByUserIdAndStatus(userDetails.getId(), status, page, size);
    }

    // todo подумать над тем, что написать отдельные response
    @GetMapping(value = "/balance")
    public BigDecimal getBalanceCard(
            @RequestParam int id,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        return cardService.getBalanceByIdAndUserId(id, userDetails.getId());
    }
}
