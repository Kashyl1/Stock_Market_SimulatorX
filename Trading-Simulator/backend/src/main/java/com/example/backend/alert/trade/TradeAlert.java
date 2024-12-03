package com.example.backend.alert.trade;

import com.example.backend.currency.Currency;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trade_alerts")
@Schema(description = "Represents a trade alert set by a user")
public class TradeAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the trade alert", example = "1")
    private Integer tradeAlertid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    @Schema(description = "The user who created the alert")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "portfolioid", nullable = false)
    @Schema(description = "The portfolio associated with the alert")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currencyid", nullable = false)
    @Schema(description = "The currency associated with the alert")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Type of the trade alert", example = "BUY")
    private TradeAlertType tradeAlertType;

    @Schema(description = "Price at which the alert should trigger", example = "50000.0")
    private BigDecimal conditionPrice;

    @Schema(description = "Amount to trade", example = "0.1")
    private BigDecimal tradeAmount;

    @Column(nullable = false)
    @Schema(description = "Indicates whether the alert is active", example = "true")
    private boolean active;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "The initial price when the alert was created", example = "48000.0")
    private BigDecimal initialPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Type of the order", example = "LIMIT")
    private OrderType orderType;
}
