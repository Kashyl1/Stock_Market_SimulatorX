package com.example.backend.currency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HistoricalRates")
public class HistoricalRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historicalRateID;

    @ManyToOne
    @JoinColumn(name = "currencyID", nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private LocalDate date;
}
