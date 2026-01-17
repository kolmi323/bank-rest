package com.example.bankcards.entity;

import com.example.bankcards.util.RequestStatus;
import com.example.bankcards.util.RequestType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "card_requests")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CardRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    CardEntity card;

    @Column(name = "request_type", nullable = false)
    @Enumerated(EnumType.STRING)
    RequestType requestType;

    @Column(name = "request_status", nullable = false)
    @Enumerated(EnumType.STRING)
    RequestStatus requestStatus;

    @Column(name = "date_create", nullable = false)
    LocalDateTime dateCreate;
}
