package com.example.backend.alert.mail;

import com.example.backend.currency.Currency;
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
@Table(name = "email_alerts")
@Schema(description = "Represents an email alert set by a user")
public class EmailAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the email alert", example = "1")
    private Integer alertid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid", nullable = false)
    @Schema(description = "The user who created the alert")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currencyid", nullable = false)
    @Schema(description = "The currency associated with the alert")
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Type of the email alert", example = "PERCENTAGE/PRIC3")
    private EmailAlertType emailAlertType;

    @Schema(description = "Percentage change for percentage alerts", example = "5.0")
    private BigDecimal percentageChange;

    @Schema(description = "Target price for price alerts", example = "50000.0")
    private BigDecimal targetPrice;

    @Column(nullable = false)
    @Schema(description = "Indicates whether the alert is active", example = "true")
    private boolean active;

    @Column(nullable = false, precision = 30, scale = 10)
    @Schema(description = "The initial price when the alert was created", example = "48000.0")
    private BigDecimal initialPrice;
}
