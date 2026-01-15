package com.example.bankcards.util.converter;

import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.entity.CardRequestEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class CardRequestEntityToCardRequestResponseConverter implements Converter<CardRequestEntity, CardRequestResponse> {
    @Override
    public CardRequestResponse convert(CardRequestEntity source) {
        return new CardRequestResponse(
                source.getId(),
                source.getCard().getNumber(),
                source.getRequestType(),
                source.getRequestStatus(),
                source.getDateCreate()
        );
    }
}
