package com.example.backend.alert;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.user.User;
import com.example.backend.currency.Currency;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer alertID;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "currencyID", nullable = false)
    private Currency currency;

    @Column(nullable = false, length = 10)
    private String alertType;

    @Column(nullable = false)
    private Double threshold;

    @Column(nullable = false)
    private Double percentageChange;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
