package com.example.bankcards.service;

import com.example.bankcards.dto.request.card.CreateCardRequest;
import com.example.bankcards.dto.response.card.BalanceCardResponse;
import com.example.bankcards.dto.response.card.CardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.SqlOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.FakerUtils;
import com.example.bankcards.util.converter.CardEntityToCardResponseConverter;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {
    @InjectMocks
    private CardService subj;

    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardEntityToCardResponseConverter converter;
    @Mock
    private FakerUtils fakerUtils;

    private CardEntity createFullCardEntity(int id, int userId, BigDecimal balance) {
        CardEntity card = new CardEntity();
        card.setId(id);
        card.setUserId(userId);
        card.setNumber("1111-2222-3333-4444");
        card.setDate(LocalDate.now().plusYears(1));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(balance);
        return card;
    }

    private CardResponse createCardResponse(int id, int userId, BigDecimal balance) {
        return new CardResponse(
                id,
                userId,
                "**** **** **** 3456",
                LocalDate.now().plusYears(1),
                CardStatus.ACTIVE,
                balance
        );
    }

    @Test
    void create_returnCardResponse_whenCalledWithValidRequest() {
        CreateCardRequest request = new CreateCardRequest();
        request.setUserId(1);
        request.setValidPeriod(LocalDate.now().plusYears(2));
        request.setBalance(new BigDecimal("1000.00"));

        String generatedNumber = "1111-2222-3333-4444";
        CardEntity card = createFullCardEntity(1, request.getUserId(), request.getBalance());
        card.setId(null);
        CardEntity savedEntity = createFullCardEntity(1, request.getUserId(), request.getBalance());
        CardResponse expectedResponse = createCardResponse(1, request.getUserId(), request.getBalance());

        when(fakerUtils.generateCardNumber()).thenReturn(generatedNumber);
        when(cardRepository.existsByNumber(generatedNumber)).thenReturn(false);
        when(cardRepository.save(card)).thenReturn(savedEntity);
        when(converter.convert(savedEntity)).thenReturn(expectedResponse);

        CardResponse result = subj.create(request);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        verify(fakerUtils, times(1)).generateCardNumber();
        verify(cardRepository, times(1)).save(card);
    }

    @Test
    void getPage_returnPageResponse_whenCalled() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        CardEntity entity = createFullCardEntity(1, 1, BigDecimal.TEN);
        CardResponse response = createCardResponse(1, 1, BigDecimal.TEN);

        when(cardRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        when(converter.convert(entity)).thenReturn(response);

        Page<CardResponse> result = subj.getPage(page, size);

        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findAll(pageable);
    }

    @Test
    void getPageByUserIdAndStatus_returnPageResponse_whenCalled() {
        int page = 0;
        int size = 10;
        int userId = 1;
        CardStatus status = CardStatus.ACTIVE;
        Pageable pageable = PageRequest.of(page, size);

        CardEntity entity = createFullCardEntity(1, userId, BigDecimal.TEN);
        when(cardRepository.findAllByUserIdAndStatus(userId, status, pageable))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(converter.convert(entity)).thenReturn(createCardResponse(1, userId, BigDecimal.TEN));

        subj.getPageByUserIdAndStatus(userId, status, page, size);

        verify(cardRepository).findAllByUserIdAndStatus(userId, status, pageable);
    }

    @Test
    void getBalanceByIdAndUserId_returnBalance_whenCardExists() {
        int cardId = 1;
        int userId = 2;
        BigDecimal balance = new BigDecimal("500.00");
        CardEntity card = createFullCardEntity(cardId, userId, balance);

        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(card));

        BalanceCardResponse result = subj.getBalanceByIdAndUserId(cardId, userId);

        assertEquals(balance, result.getBalance());
    }

    @Test
    void getBalanceByIdAndUserId_throwNotFoundException_whenCardNotFound() {
        int cardId = 1;
        int userId = 2;

        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subj.getBalanceByIdAndUserId(cardId, userId));
    }

    @Test
    void deleteByIdAndUserId_success_whenOneRecordDeleted() {
        int cardId = 1;
        int userId = 2;

        when(cardRepository.deleteByIdAndUserId(cardId, userId)).thenReturn(1);

        assertDoesNotThrow(() -> subj.deleteByIdAndUserId(cardId, userId));
        verify(cardRepository).deleteByIdAndUserId(cardId, userId);
    }

    @Test
    void deleteByIdAndUserId_throwNotFoundException_whenNoRecordDeleted() {
        int cardId = 1;
        int userId = 2;

        when(cardRepository.deleteByIdAndUserId(cardId, userId)).thenReturn(0);

        assertThrows(NotFoundException.class, () -> subj.deleteByIdAndUserId(cardId, userId));
    }

    @Test
    void deleteByIdAndUserId_throwSqlOperationException_whenMultipleRecordsDeleted() {
        int cardId = 1;
        int userId = 2;

        when(cardRepository.deleteByIdAndUserId(cardId, userId)).thenReturn(2);

        assertThrows(SqlOperationException.class, () -> subj.deleteByIdAndUserId(cardId, userId));
    }

    @Test
    void updateFromCard_success_whenBalanceSufficient() {
        int userId = 1;
        int cardId = 10;
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal initialBalance = new BigDecimal("150.00");

        CardEntity card = createFullCardEntity(cardId, userId, initialBalance);

        when(cardRepository.findByIdAndUserIdAndBalanceIsGreaterThanEqual(cardId, userId, amount))
                .thenReturn(Optional.of(card));

        subj.updateFromCard(userId, cardId, amount);

        assertEquals(new BigDecimal("50.00"), card.getBalance());
        verify(cardRepository).save(card);
    }

    @Test
    void updateFromCard_throwNotFoundException_whenBalanceInsufficientOrCardNotFound() {
        int userId = 1;
        int cardId = 10;
        BigDecimal amount = new BigDecimal("100.00");

        when(cardRepository.findByIdAndUserIdAndBalanceIsGreaterThanEqual(cardId, userId, amount))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subj.updateFromCard(userId, cardId, amount));
        verify(cardRepository, never()).save(any());
    }

    @Test
    void updateToCard_success_whenCardExists() {
        int userId = 1;
        int cardId = 10;
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal initialBalance = new BigDecimal("50.00");

        CardEntity card = createFullCardEntity(cardId, userId, initialBalance);

        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.of(card));

        subj.updateToCard(userId, cardId, amount);

        assertEquals(new BigDecimal("150.00"), card.getBalance());
        verify(cardRepository).save(card);
    }

    @Test
    void updateToCard_throwNotFoundException_whenCardNotFound() {
        int userId = 1;
        int cardId = 10;
        BigDecimal amount = new BigDecimal("100.00");

        when(cardRepository.findByIdAndUserId(cardId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subj.updateToCard(userId, cardId, amount));
        verify(cardRepository, never()).save(any());
    }
}