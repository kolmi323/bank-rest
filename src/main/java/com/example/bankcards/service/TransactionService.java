package com.example.bankcards.service;

import com.example.bankcards.dto.request.card.CreateTransactionRequest;
import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.TransactionEntity;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.repository.TransactionRepository;
import com.example.bankcards.util.converter.TransactionEntityToTransactionResponseConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TransactionService {
    TransactionRepository transactionRepository;
    CardService cardService;
    TransactionEntityToTransactionResponseConverter converter;

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request, Integer userId) {
        validateCards(request.getFromCardId(), request.getToCardId());
        return insert(request, userId);
    }

    private TransactionResponse insert(CreateTransactionRequest request, int userId) {
        cardService.updateFromCard(userId, request.getFromCardId(), request.getAmount());
        cardService.updateToCard(userId, request.getToCardId(), request.getAmount());
        TransactionEntity transaction = new TransactionEntity();
        transaction.setFromCardId(request.getFromCardId());
        transaction.setToCardId(request.getToCardId());
        transaction.setAmount(request.getAmount());
        transaction.setDate(LocalDateTime.now());
        return converter.convert(transactionRepository.save(transaction));
    }

    private void validateCards (int fromCardId, int toCardId) {
        if (fromCardId == toCardId) {
            throw new BadRequestException("Недопустимая операция: идентификарторы карт одинаковы");
        }
    }
}
