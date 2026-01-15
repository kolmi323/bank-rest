package com.example.bankcards.entity;

import com.example.bankcards.util.CardStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "user_id", nullable = false)
    Integer userId;

    @Column(name = "number", nullable = false)
    String number;

    @Column(name = "valid_period", nullable = false)
    LocalDate date;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    CardStatus status;

    @Column(name = "balance", nullable = false)
    BigDecimal balance;
}
