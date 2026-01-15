package com.example.bankcards.util.converter;

import com.example.bankcards.dto.response.TransactionResponse;
import com.example.bankcards.entity.TransactionEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class TransactionEntityToTransactionResponseConverter implements Converter<TransactionEntity, TransactionResponse> {
    @Override
    public TransactionResponse convert(TransactionEntity source) {
        return new TransactionResponse(
                source.getId(),
                source.getFromCardId(),
                source.getToCardId(),
                source.getAmount(),
                source.getDate()
        );
    }
}
