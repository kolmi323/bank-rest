package com.example.bankcards.controller.admin;

import com.example.bankcards.controller.ApiController;
import com.example.bankcards.dto.request.card.CreateCardRequest;
import com.example.bankcards.dto.request.card.DeleteCardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank/admin/card")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminCardController extends ApiController {
    CardService cardService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(
            @RequestBody @Valid CreateCardRequest request
    ) {
        return cardService.create(request);
    }

    @PostMapping("/delete")
    public void deleteCard(@RequestBody @Valid DeleteCardRequest request) {
        cardService.deleteByIdAndUserId(request.getCardId(), request.getUserId());
    }

    @GetMapping("/view")
    public Page<CardResponse> viewCards(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return cardService.getPage(page, size);
    }

    @GetMapping("/filter/status")
    public Page<CardResponse> filterCardsByStatus(@RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size,
                                               @RequestParam(required = false, defaultValue = "ACTIVE") CardStatus status) {
        return cardService.getPageByStatus(status, page, size);
    }

    @GetMapping("/filter/user")
    public Page<CardResponse> filterCardsByStatus(@RequestParam(required = false, defaultValue = "0") int page,
                                               @RequestParam(required = false, defaultValue = "10") int size,
                                               @RequestParam(required = false, defaultValue = "1", name = "user_id") int userId) {
        return cardService.getPageByUserId(userId, page, size);
    }
}
