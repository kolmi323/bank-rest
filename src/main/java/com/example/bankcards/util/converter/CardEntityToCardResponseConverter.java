package com.example.bankcards.util.converter;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class CardEntityToCardResponseConverter implements Converter<CardEntity, CardResponse> {
    @Override
    public CardResponse convert(CardEntity source) {
        return new CardResponse(
                source.getId(),
                source.getUserId(),
                source.getNumber(),
                source.getDate(),
                source.getStatus(),
                source.getBalance()
        );
    }
}
