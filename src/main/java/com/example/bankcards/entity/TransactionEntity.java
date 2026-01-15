package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "from_card_id", nullable = false)
    Integer fromCardId;

    @Column(name = "to_card_id", nullable = false)
    Integer toCardId;

    @Column(name = "amount", nullable = false)
    BigDecimal amount;

    @Column(name = "date", nullable = false)
    LocalDateTime date;
}
