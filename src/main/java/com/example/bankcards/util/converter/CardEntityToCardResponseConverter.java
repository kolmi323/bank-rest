package com.example.bankcards.util.converter;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.util.CardUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardEntityToCardResponseConverter implements Converter<CardEntity, CardResponse> {
    CardUtils cardUtils;

    @Override
    public CardResponse convert(CardEntity source) {
        return new CardResponse(
                source.getId(),
                source.getUserId(),
                cardUtils.maskingNumber(source.getNumber()),
                source.getDate(),
                source.getStatus(),
                source.getBalance()
        );
    }
}
