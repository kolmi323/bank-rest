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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardRequestServiceTest {
    @InjectMocks private CardRequestService subj;

    @Mock private CardRequestRepository cardRequestRepository;
    @Mock private CardService cardService;
    @Mock private CardRequestEntityToCardRequestResponseConverter converter;

    private CardEntity createCardEntity(int id, CardStatus status) {
        CardEntity card = new CardEntity();
        card.setId(id);
        card.setUserId(1);
        card.setNumber("1234");
        card.setDate(LocalDate.now());
        card.setStatus(status);
        card.setBalance(BigDecimal.ZERO);
        return card;
    }

    private CardRequestEntity createRequestEntity(int id, CardEntity card, RequestType type, RequestStatus status) {
        CardRequestEntity request = new CardRequestEntity();
        request.setId(id);
        request.setCard(card);
        request.setRequestType(type);
        request.setRequestStatus(status);
        request.setDateCreate(LocalDateTime.now());
        return request;
    }

    private CardRequestResponse createResponse(int id, RequestType type, RequestStatus status) {
        return new CardRequestResponse(id, "**** **** **** 1234", type, status, LocalDateTime.now());
    }

    @Test
    void createRequest_returnResponse_whenRequestIsBlockAndCardIsActive() {
        int userId = 1;
        CreateCardReqRequest request = new CreateCardReqRequest();
        request.setCardId(1);
        request.setRequestType(RequestType.BLOCK);
        CardEntity card = createCardEntity(1, CardStatus.ACTIVE);
        CardRequestEntity cardRequest = createRequestEntity(1, card, RequestType.BLOCK, RequestStatus.WAITING);
        cardRequest.setId(null);
        CardRequestEntity savedEntity = createRequestEntity(1, card, RequestType.BLOCK, RequestStatus.WAITING);
        CardRequestResponse expectedResponse = createResponse(1, RequestType.BLOCK, RequestStatus.WAITING);

        when(cardService.handleCardByIdAndUserId(request.getCardId(), userId)).thenReturn(card);
        when(cardRequestRepository.save(cardRequest)).thenReturn(savedEntity);
        when(converter.convert(savedEntity)).thenReturn(expectedResponse);

        CardRequestResponse result = subj.createRequestForChangeStatusCard(request, userId);

        assertNotNull(result);
        assertEquals(expectedResponse.getRequestStatus(), result.getRequestStatus());
        verify(cardRequestRepository, times(1)).save(cardRequest);
    }

    @Test
    void createRequest_throwBadRequest_whenCardActiveAndRequestUnblock() {
        int userId = 1;
        CreateCardReqRequest request = new CreateCardReqRequest();
        request.setCardId(1);
        request.setRequestType(RequestType.UNBLOCK);
        CardEntity card = createCardEntity(1, CardStatus.ACTIVE);

        when(cardService.handleCardByIdAndUserId(request.getCardId(), userId)).thenReturn(card);

        assertThrows(BadRequestException.class, () -> subj.createRequestForChangeStatusCard(request, userId));
        verify(cardRequestRepository, never()).save(any());
    }

    @Test
    void acceptRequest_changeCardToBlock_whenRequestIsBlockAndWaiting() {
        int requestId = 2;
        CardEntity card = createCardEntity(1, CardStatus.ACTIVE);
        CardRequestEntity requestEntity = createRequestEntity(requestId, card, RequestType.BLOCK, RequestStatus.WAITING);
        CardRequestResponse response = createResponse(requestId, RequestType.BLOCK, RequestStatus.ACCEPTED);

        when(cardRequestRepository.findById(requestId)).thenReturn(Optional.of(requestEntity));
        when(cardRequestRepository.save(requestEntity)).thenReturn(requestEntity);
        when(converter.convert(requestEntity)).thenReturn(response);

        CardRequestResponse result = subj.acceptRequest(requestId);

        assertEquals(result, response);
        assertEquals(CardStatus.BLOCK, card.getStatus());
        assertEquals(RequestStatus.ACCEPTED, requestEntity.getRequestStatus());
        verify(cardRequestRepository).save(requestEntity);
    }

    @Test
    void acceptRequest_throwBadRequest_whenRequestIsNotWaiting() {
        int requestId = 2;
        CardEntity card = createCardEntity(1, CardStatus.ACTIVE);
        CardRequestEntity requestEntity = createRequestEntity(requestId, card, RequestType.BLOCK, RequestStatus.REJECTED);

        when(cardRequestRepository.findById(requestId)).thenReturn(Optional.of(requestEntity));

        assertThrows(BadRequestException.class, () -> subj.acceptRequest(requestId));
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRequestRepository, never()).save(any());
    }

    @Test
    void acceptRequest_throwNotFound_whenRequestNotExist() {
        int requestId = 3;
        when(cardRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subj.acceptRequest(requestId));
    }

    @Test
    void rejectRequest_changeStatusToRejected_whenCalled() {
        int requestId = 3;
        CardEntity card = createCardEntity(1, CardStatus.ACTIVE);
        CardRequestEntity requestEntity = createRequestEntity(requestId, card, RequestType.BLOCK, RequestStatus.WAITING);
        CardRequestResponse response = createResponse(requestId, RequestType.BLOCK, RequestStatus.ACCEPTED);

        when(cardRequestRepository.findById(requestId)).thenReturn(Optional.of(requestEntity));
        when(cardRequestRepository.save(requestEntity)).thenReturn(requestEntity);
        when(converter.convert(requestEntity)).thenReturn(response);

        CardRequestResponse result = subj.rejectRequest(requestId);

        assertEquals(result, response);
        assertEquals(RequestStatus.REJECTED, requestEntity.getRequestStatus());
        assertEquals(CardStatus.ACTIVE, card.getStatus());
        verify(cardRequestRepository).save(requestEntity);
    }

    @Test
    void rejectRequest_throwNotFound_whenRequestNotExist() {
        when(cardRequestRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> subj.rejectRequest(1));
    }

    @Test
    void getPageByCardIdAndRequestStatus_returnPage_whenCalled() {
        int page = 0;
        int size = 10;
        int cardId = 10;
        RequestStatus status = RequestStatus.WAITING;
        Pageable pageable = PageRequest.of(page, size);
        CardRequestEntity entity = new CardRequestEntity();

        when(cardRequestRepository.findByCardIdAndRequestStatus(cardId, status, pageable))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(converter.convert(entity)).thenReturn(mock(CardRequestResponse.class));

        Page<CardRequestResponse> result = subj.getPageByCardIdAndRequestStatus(cardId, status, page, size);

        assertEquals(1, result.getTotalElements());
        verify(cardRequestRepository).findByCardIdAndRequestStatus(cardId, status, pageable);
    }
}