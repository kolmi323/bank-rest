package com.example.bankcards.service;

import com.example.bankcards.dto.request.card.CreateCardRequest;
import com.example.bankcards.dto.response.card.BalanceCardResponse;
import com.example.bankcards.dto.response.card.CardResponse;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.SqlOperationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardStatus;
import com.example.bankcards.util.FakerUtils;
import com.example.bankcards.util.converter.CardEntityToCardResponseConverter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CardService {
    CardRepository cardRepository;
    CardEntityToCardResponseConverter converter;
    FakerUtils fakerUtils;

    public CardResponse create(CreateCardRequest createCardRequest) {
        CardEntity card = new CardEntity();
        card.setUserId(createCardRequest.getUserId());
        card.setNumber(createCardNumber());
        card.setDate(createCardRequest.getValidPeriod());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(createCardRequest.getBalance());
        return converter.convert(cardRepository.save(card));
    }

    public Page<CardResponse> getPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findAll(pageable).map(converter::convert);
    }

    public Page<CardResponse> getPageByUserId(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findAllByUserId(userId, pageable).map(converter::convert);
    }

    public Page<CardResponse> getPageByUserIdAndStatus(int userId, CardStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findAllByUserIdAndStatus(userId, status, pageable).map(converter::convert);
    }

    public Page<CardResponse> getPageByStatus(CardStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cardRepository.findAllByStatus(status, pageable).map(converter::convert);
    }

    public BalanceCardResponse getBalanceByIdAndUserId(int cardId, int userId) {
        return new BalanceCardResponse(handleCardByIdAndUserId(cardId, userId).getBalance());
    }

    @Transactional(rollbackFor = {NotFoundException.class, IllegalArgumentException.class})
    public void deleteByIdAndUserId(int cardId, int userId) {
        int countModifiedRecords = cardRepository.deleteByIdAndUserId(cardId, userId);
        if (countModifiedRecords == 0) {
            throw new NotFoundException(String.format("Карта %d пользователя %d не найдена", cardId, userId));
        } else if (countModifiedRecords > 1) {
            throw new SqlOperationException(String.format("Удаление карты %d пользователя %d провалено", cardId, userId));
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateFromCard(int userId, int fromCardId, BigDecimal amount) {
        CardEntity card = handleCardByIdAndUserIdAndBalanceIsGreaterThanEqual(fromCardId, userId, amount);
        validCardForTransaction(card);
        card.setBalance(card.getBalance().subtract(amount));
        cardRepository.save(card);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateToCard(int userId, int toCardId, BigDecimal amount) {
        CardEntity card = handleCardByIdAndUserId(toCardId, userId);
        validCardForTransaction(card);
        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);
    }

    public CardEntity handleCardByIdAndUserId(int id, int userId) {
        return cardRepository.findByIdAndUserId(id, userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Карты <%d> для пользователя <%d> не существует", id, userId)
                )
        );
    }

    public CardEntity handleCardByIdAndUserIdAndBalanceIsGreaterThanEqual(int id, int userId, BigDecimal amount) {
        return cardRepository.findByIdAndUserIdAndBalanceIsGreaterThanEqual(id, userId, amount).orElseThrow(
                () -> new NotFoundException(
                        String.format("Карты <%d> для пользователя <%d> c необходимым балансом не найдена", id, userId)
                )
        );
    }

    private String createCardNumber() {
        String number = fakerUtils.generateCardNumber();
        while(cardRepository.existsByNumber(number)) {
            number = fakerUtils.generateCardNumber();
        }
        return number;
    }

    private void validCardForTransaction(CardEntity card) {
        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException(String.format("карта (%d) недоступна для перевода", card.getId()));
        }
    }
}
