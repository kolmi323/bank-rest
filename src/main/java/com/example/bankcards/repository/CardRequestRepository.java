package com.example.bankcards.repository;

import com.example.bankcards.entity.CardRequestEntity;
import com.example.bankcards.util.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRequestRepository extends JpaRepository<CardRequestEntity, Integer> {
    Page<CardRequestEntity> findByCardId(int cardId, Pageable pageable);
    Page<CardRequestEntity> findByRequestStatus(RequestStatus requestStatus, Pageable pageable);
    Page<CardRequestEntity> findByCardIdAndRequestStatus(int cardId, RequestStatus requestStatus, Pageable pageable);
 }
