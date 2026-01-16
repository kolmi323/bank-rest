package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.dto.request.request.CreateCardReqRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardRequestEntity;
import com.example.bankcards.repository.CardRepository;
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
import java.util.Optional;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardRequestService {
    CardRequestRepository cardRequestRepository;
    CardRepository cardRepository;
    CardRequestEntityToCardRequestResponseConverter converter;

    public CardRequestResponse createRequestForChangeStatusCard(CreateCardReqRequest request, int userId) {
        CardEntity card = cardRepository.findByIdAndUserId(request.getCardId(), userId);
        if (checkCorrectStatusRequest(card.getStatus(), request.getRequestType())) {
            CardRequestEntity cardRequest = new CardRequestEntity();
            cardRequest.setCard(card);
            cardRequest.setRequestType(request.getRequestType());
            cardRequest.setRequestStatus(RequestStatus.WAITING);
            cardRequest.setDateCreate(LocalDateTime.now());
            CardRequestResponse cardRequestResponse = converter.convert(cardRequestRepository.save(cardRequest));
            return cardRequestResponse;
        } else {
            throw new IllegalArgumentException("Такое изменение статуса невозможно");
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
        Optional<CardRequestEntity> cardRequest = cardRequestRepository.findById(requestId);
        if (cardRequest.isPresent() && cardRequest.get().getRequestStatus() == RequestStatus.WAITING) {
            CardRequestEntity request = cardRequest.get();
            if (request.getRequestType() == RequestType.BLOCK) {
                request.getCard().setStatus(CardStatus.BLOCK);
                request.setRequestStatus(RequestStatus.ACCEPTED);
            } else if (request.getRequestType() == RequestType.UNBLOCK) {
                request.getCard().setStatus(CardStatus.ACTIVE);
                request.setRequestStatus(RequestStatus.ACCEPTED);
            }
            return converter.convert(cardRequestRepository.save(request));
        } else {
            throw new IllegalArgumentException("Запрос некорректный");
        }
    }

    @Transactional
    public CardRequestResponse rejectRequest(int requestId) {
        Optional<CardRequestEntity> cardRequest = cardRequestRepository.findById(requestId);
        if (cardRequest.isPresent()) {
            CardRequestEntity request = cardRequest.get();
            request.setRequestStatus(RequestStatus.REJECTED);
            return converter.convert(cardRequestRepository.save(request));
        } else {
            throw new IllegalArgumentException("Такого запроса не существует");
        }
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
