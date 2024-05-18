package com.example.backend.analysis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.currency.Currency;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TechnicalIndicators")
public class TechnicalIndicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer indicatorID;

    @ManyToOne
    @JoinColumn(name = "currencyID", nullable = false)
    private Currency currency;

    @Column(nullable = false, length = 100)
    private String indicatorType;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private LocalDate date;
}
