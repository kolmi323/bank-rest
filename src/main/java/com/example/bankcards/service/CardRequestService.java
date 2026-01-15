package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.dto.request.CreateCardRequestTypeRequest;
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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardRequestService {
    CardRequestRepository cardRequestRepository;
    CardRepository cardRepository;
    CardRequestEntityToCardRequestResponseConverter converter;

    public CardRequestResponse createRequestForChangeStatusCard(CreateCardRequestTypeRequest request, Integer userId) {
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
            return null; //todo нормальную обработку
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
