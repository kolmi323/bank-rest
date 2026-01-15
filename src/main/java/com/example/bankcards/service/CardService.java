package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.request.CreateCardRequest;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.converter.CardEntityToCardResponseConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.datafaker.Faker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardService {
    CardRepository cardRepository;
    CardEntityToCardResponseConverter converter;

    public CardResponse create(CreateCardRequest createCardRequest) {
        CardEntity card = new CardEntity();
        card.setUserId(createCardRequest.getUserId());
        card.setNumber(generateCardNumber());
        card.setDate(createCardRequest.getValidPeriod());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(createCardRequest.getBalance());
        return converter.convert(cardRepository.save(card));
    }

    public Page<CardResponse> getPageByUserId(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findAllByUserId(userId, pageable).map(converter::convert);
    }

    public Page<CardResponse> getPageByUserIdAndStatus(Integer userId, CardStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findAllByUserIdAndStatus(userId, status, pageable).map(converter::convert);
    }

    public BigDecimal getCardBalanceByIdAndUserId(Integer cardId, Integer userId) {
        return cardRepository.findByIdAndUserId(cardId, userId).getBalance();
    }

    public void updateFromCard(int userId, int fromCardId, BigDecimal amount) {
        CardEntity card = cardRepository.findByIdAndUserIdAndBalanceIsGreaterThanEqual(fromCardId, userId, amount);
        card.setBalance(card.getBalance().subtract(amount));
        cardRepository.save(card);
    }

    public void updateToAccount(int userId, int toCardId, BigDecimal amount) {
        CardEntity card = cardRepository.findByIdAndUserId(toCardId, userId);
        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);
    }

    private String generateCardNumber() {
        Faker faker = new Faker();
        String number = faker.finance().creditCard();
        while(cardRepository.existsByNumber(number)) {
            number = faker.finance().creditCard();
        }
        return number;
    }
}
