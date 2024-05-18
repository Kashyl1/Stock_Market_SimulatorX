package com.example.backend.currency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CurrentRates")
public class CurrentRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rateID;

    @ManyToOne
    @JoinColumn(name = "currencyID", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
