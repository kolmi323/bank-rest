package com.example.bankcards.util.converter;

import com.example.bankcards.dto.response.CardRequestResponse;
import com.example.bankcards.entity.CardRequestEntity;
import com.example.bankcards.util.CardUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardRequestEntityToCardRequestResponseConverter implements Converter<CardRequestEntity, CardRequestResponse> {
    CardUtils cardUtils;

    @Override
    public CardRequestResponse convert(CardRequestEntity source) {
        return new CardRequestResponse(
                source.getId(),
                cardUtils.maskingNumber(source.getCard().getNumber()),
                source.getRequestType(),
                source.getRequestStatus(),
                source.getDateCreate()
        );
    }
}
