package com.example.bankcards.service;

import com.example.bankcards.dto.request.req.CreateTransactionRequest;
import com.example.bankcards.dto.response.card.TransactionResponse;
import com.example.bankcards.entity.TransactionEntity;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.converter.TransactionEntityToTransactionResponseConverter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks private TransactionService subj;

    @Mock private TransactionRepository transactionRepository;
    @Mock private CardService cardService;
    @Mock private TransactionEntityToTransactionResponseConverter converter;

    private CreateTransactionRequest createFullRequest(int fromId, int toId, BigDecimal amount) {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setFromCardId(fromId);
        request.setToCardId(toId);
        request.setAmount(amount);
        return request;
    }

    private TransactionEntity createFullEntity(int id, int fromId, int toId, BigDecimal amount) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(id);
        entity.setFromCardId(fromId);
        entity.setToCardId(toId);
        entity.setAmount(amount);
        entity.setDate(LocalDateTime.now());
        return entity;
    }

    @Test
    void create_returnTransactionResponse_whenRequestIsValid() {
        int userId = 1;
        int fromCardId = 1;
        int toCardId = 2;
        BigDecimal amount = new BigDecimal("100.00");

        CreateTransactionRequest request = createFullRequest(fromCardId, toCardId, amount);
        TransactionEntity transaction = createFullEntity(userId, fromCardId, toCardId, amount);
        transaction.setId(null);
        TransactionEntity savedEntity = createFullEntity(55, fromCardId, toCardId, amount);
        TransactionResponse expectedResponse = new TransactionResponse(
                savedEntity.getId(),
                savedEntity.getFromCardId(),
                savedEntity.getToCardId(),
                savedEntity.getAmount(),
                savedEntity.getDate()
        );

        when(transactionRepository.save(transaction)).thenReturn(savedEntity);
        when(converter.convert(savedEntity)).thenReturn(expectedResponse);

        TransactionResponse result = subj.create(request, userId);

        assertNotNull(result);
        assertEquals(expectedResponse.getId(), result.getId());
        assertEquals(expectedResponse.getAmount(), result.getAmount());
        assertEquals(expectedResponse.getFromCardId(), result.getFromCardId());
        verify(cardService, times(1)).updateFromCard(userId, fromCardId, amount);
        verify(cardService, times(1)).updateToCard(userId, toCardId, amount);
        verify(transactionRepository, times(1)).save(transaction);
        verify(converter, times(1)).convert(savedEntity);
    }

    @Test
    void create_throwBadRequestException_whenCardIdsAreEqual() {
        int userId = 1;
        int sameId = 1;
        BigDecimal amount = new BigDecimal("500.00");

        CreateTransactionRequest request = createFullRequest(sameId, sameId, amount);

        assertThrows(BadRequestException.class, () -> subj.create(request, userId));
        verifyNoInteractions(cardService);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(converter);
    }

    @Test
    void create_propagateException_whenCardServiceUpdateFromFails() {
        int userId = 1;
        int fromCardId = 1;
        int toCardId = 2;
        BigDecimal amount = new BigDecimal("100.00");
        CreateTransactionRequest request = createFullRequest(fromCardId, toCardId, amount);

        doThrow(new NotFoundException(
                String.format("Карты <%d> для пользователя <%d> c необходимым балансом не найдена", fromCardId, userId)
        )).when(cardService).updateFromCard(userId, fromCardId, amount);

        assertThrows(NotFoundException.class, () -> subj.create(request, userId));
        verify(cardService, times(1)).updateFromCard(userId, fromCardId, amount);
        verify(cardService, never()).updateToCard(anyInt(), anyInt(), any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_propagateException_whenCardServiceUpdateToFails() {
        int userId = 1;
        int fromCardId = 1;
        int toCardId = 2;
        BigDecimal amount = new BigDecimal("100.00");
        CreateTransactionRequest request = createFullRequest(fromCardId, toCardId, amount);

        doThrow(new NotFoundException(String.format(
                "Карты <%d> для пользователя <%d> не существует", toCardId, userId)
        )).when(cardService).updateToCard(userId, toCardId, amount);

        assertThrows(NotFoundException.class, () -> subj.create(request, userId));

        verify(cardService, times(1)).updateFromCard(userId, fromCardId, amount);
        verify(cardService, times(1)).updateToCard(userId, toCardId, amount);
        verify(transactionRepository, never()).save(any());
    }
}