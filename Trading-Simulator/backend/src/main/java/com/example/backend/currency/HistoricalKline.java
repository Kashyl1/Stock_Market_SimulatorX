package com.example.backend.currency;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "HistoricalKlines",
        indexes = {
                @Index(name = "idx_currency_timeInterval_openTime", columnList = "currencyid, interval, openTime"),
                @Index(name = "idx_currency", columnList = "currencyid"),
                @Index(name = "idx_timeInterval", columnList = "interval"),
                @Index(name = "idx_openTime", columnList = "openTime")
        }
)
@Schema(description = "Represents historical candlestick data for a currency")
public class HistoricalKline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the historical kline", example = "1")
    private Long historicalKlinesid;

    @ManyToOne
    @JoinColumn(name = "currencyid", nullable = false)
    @Schema(description = "Currency associated with this historical kline")
    private Currency currency;

    @Column(nullable = false)
    @Schema(description = "Open time of the kline in milliseconds", example = "1625097600000")
    private Long openTime;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "Open price of the kline", example = "50000.00")
    private BigDecimal openPrice;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "High price of the kline", example = "51000.00")
    private BigDecimal highPrice;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "Low price of the kline", example = "49000.00")
    private BigDecimal lowPrice;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "Close price of the kline", example = "50500.00")
    private BigDecimal closePrice;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "Volume during the kline", example = "1200.50")
    private BigDecimal volume;

    @Column(nullable = false)
    @Schema(description = "Close time of the kline in milliseconds", example = "1625101200000")
    private Long closeTime;

    @Column(name = "`interval`", nullable = false, length = 5)
    @Schema(description = "Interval of the kline", example = "1h")
    private String timeInterval;

    @Version
    @Column(nullable = false)
    private Long version = 0L;
}
