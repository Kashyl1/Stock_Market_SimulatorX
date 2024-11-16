package com.example.backend.currency;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HistoricalRates")
@Schema(description = "Represents historical exchange rates for a currency")
public class HistoricalRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the historical rate", example = "1")
    private Integer historicalRateid;

    @ManyToOne
    @JoinColumn(name = "currencyid", nullable = false)
    @Schema(description = "Currency associated with this historical rate")
    private Currency currency;

    @Column(nullable = false)
    @Schema(description = "Exchange rate on the given date", example = "50000.00")
    private Double rate;

    @Column(nullable = false)
    @Schema(description = "Date of the historical rate", example = "2023-10-01")
    private LocalDate date;
}
