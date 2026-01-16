package com.example.bankcards.controller.admin;

import com.example.bankcards.controller.ApiController;
import com.example.bankcards.dto.request.request.ChangeCardReqRequest;
import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.service.CardRequestService;
import com.example.bankcards.util.RequestStatus;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank/admin/request")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminRequestCardController extends ApiController {
    CardRequestService cardRequestService;

    @PostMapping("/accept")
    public CardRequestResponse acceptRequest(@RequestBody @Valid ChangeCardReqRequest request) {
        return cardRequestService.acceptRequest(request.getRequestId());
    }

    @PostMapping("/reject")
    public CardRequestResponse rejectRequest(@RequestBody @Valid ChangeCardReqRequest request) {
        return cardRequestService.rejectRequest(request.getRequestId());
    }

    @GetMapping("/view")
    public Page<CardRequestResponse> viewRequests(@RequestParam(required = false, defaultValue = "0") int page,
                                                  @RequestParam(required = false, defaultValue = "10") int size) {
        return cardRequestService.getPage(page, size);
    }

    @GetMapping("/filter/status")
    public Page<CardRequestResponse> filterRequestsByStatus(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "WAITING") RequestStatus status
    ) {
        return cardRequestService.getPageByRequestStatus(status, page, size);
    }

    @GetMapping("/filter/card")
    public Page<CardRequestResponse> filterRequestsByCardId(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "1", name = "card_id") int cardId
    ) {
        return cardRequestService.getPageByCardId(cardId, page, size);
    }

    @GetMapping("/filter/main")
    public Page<CardRequestResponse> filterRequestsByCardIdAndStatus(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "1", name = "card_id") int cardId,
            @RequestParam(required = false, defaultValue = "WAITING") RequestStatus status
    ) {
        return cardRequestService.getPageByCardIdAndRequestStatus(cardId, status, page, size);
    }
}
