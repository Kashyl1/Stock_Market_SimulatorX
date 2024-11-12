package com.example.backend.alert.trade;

import com.example.backend.currency.Currency;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trade_alerts")
public class TradeAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tradeAlertId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolioid", nullable = false)
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currencyid", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeAlertType tradeAlertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertConditionType conditionType;

    private BigDecimal conditionValue;

    private BigDecimal tradeAmount;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal initialPrice;
}
