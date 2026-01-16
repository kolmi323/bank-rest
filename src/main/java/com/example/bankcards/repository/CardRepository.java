package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.util.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Integer> {
    Page<CardEntity> findAllByUserId(Integer userId, Pageable pageable);
    Page<CardEntity> findAllByStatus(CardStatus status, Pageable pageable);
    Page<CardEntity> findAllByUserIdAndStatus(Integer userId, CardStatus status, Pageable pageable);
    CardEntity findByIdAndUserId(Integer id, Integer userId);
    Integer deleteByIdAndUserId(Integer id, Integer userId);
    CardEntity findByIdAndUserIdAndBalanceIsGreaterThanEqual(Integer id, Integer userId, BigDecimal balance);
    boolean existsByNumber(String number);
}
