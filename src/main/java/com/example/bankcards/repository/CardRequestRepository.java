package com.example.bankcards.repository;

import com.example.bankcards.entity.CardRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRequestRepository extends JpaRepository<CardRequestEntity, Integer> {
}
