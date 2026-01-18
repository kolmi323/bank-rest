package com.example.bankcards.service;

import com.example.bankcards.dto.request.req.CreateCardReqRequest;
import com.example.bankcards.dto.response.request.CardRequestResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardRequestEntity;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRequestRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.RequestStatus;
import com.example.bankcards.util.RequestType;
import com.example.bankcards.util.converter.CardRequestEntityToCardRequestResponseConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardRequestService {
    CardRequestRepository cardRequestRepository;
    CardService cardService;
    CardRequestEntityToCardRequestResponseConverter converter;

    public CardRequestResponse createRequestForChangeStatusCard(CreateCardReqRequest request, int userId) {
        CardEntity card = cardService.handleCardByIdAndUserId(request.getCardId(), userId);
        if (checkCorrectStatusRequest(card.getStatus(), request.getRequestType())) {
            CardRequestEntity cardRequest = new CardRequestEntity();
            cardRequest.setCard(card);
            cardRequest.setRequestType(request.getRequestType());
            cardRequest.setRequestStatus(RequestStatus.WAITING);
            cardRequest.setDateCreate(LocalDateTime.now());
            return converter.convert(cardRequestRepository.save(cardRequest));
        } else {
            throw new BadRequestException("Такое изменение статуса невозможно");
        }
    }

    public Page<CardRequestResponse> getPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRequestRepository.findAll(pageable).map(converter::convert);
    }

    public Page<CardRequestResponse> getPageByCardId(int cardId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRequestRepository
                .findByCardId(cardId, pageable)
                .map(converter::convert);
    }

    public Page<CardRequestResponse> getPageByRequestStatus(RequestStatus requestStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRequestRepository
                .findByRequestStatus(requestStatus, pageable)
                .map(converter::convert);
    }

    public Page<CardRequestResponse> getPageByCardIdAndRequestStatus(int cardId, RequestStatus requestStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRequestRepository
                .findByCardIdAndRequestStatus(cardId, requestStatus, pageable)
                .map(converter::convert);
    }

    @Transactional
    public CardRequestResponse acceptRequest(int requestId) {
        CardRequestEntity cardRequest = handleCardRequestById(requestId);
        if (cardRequest.getRequestStatus() == RequestStatus.WAITING) {
            if (cardRequest.getRequestType() == RequestType.BLOCK) {
                cardRequest.getCard().setStatus(CardStatus.BLOCK);
                cardRequest.setRequestStatus(RequestStatus.ACCEPTED);
            } else if (cardRequest.getRequestType() == RequestType.UNBLOCK) {
                cardRequest.getCard().setStatus(CardStatus.ACTIVE);
                cardRequest.setRequestStatus(RequestStatus.ACCEPTED);
            }
            return converter.convert(cardRequestRepository.save(cardRequest));
        } else {
            throw new BadRequestException("Запрос некорректный");
        }
    }

    public CardRequestResponse rejectRequest(int requestId) {
        CardRequestEntity request = handleCardRequestById(requestId);
        request.setRequestStatus(RequestStatus.REJECTED);
        return converter.convert(cardRequestRepository.save(request));
    }

    public CardRequestEntity handleCardRequestById(int id) {
        return cardRequestRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Такого запроса не существует")
        );
    }

    private boolean checkCorrectStatusRequest(CardStatus cardStatus, RequestType requestType) {
        if (cardStatus == CardStatus.ACTIVE && requestType == RequestType.UNBLOCK) {
            return false;
        } else if (cardStatus == CardStatus.BLOCK && requestType == RequestType.BLOCK) {
            return false;
        } else {
            return true;
        }
    }
}
