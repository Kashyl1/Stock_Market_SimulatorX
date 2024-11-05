package com.example.backend.alert;

import com.example.backend.currency.Currency;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currencyid", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    private BigDecimal percentageChange;

    private BigDecimal targetPrice;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal initialPrice;
}
