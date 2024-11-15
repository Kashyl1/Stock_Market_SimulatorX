package com.example.backend.alert.mail;

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
@Table(name = "email_alerts")
public class EmailAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currencyid", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailAlertType emailAlertType;

    private BigDecimal percentageChange;

    private BigDecimal targetPrice;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, precision = 30, scale = 10)
    private BigDecimal initialPrice;
}
