package com.example.backend.alert.mail;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EmailAlertResponse {
    private Integer alertId;
    private Integer currencyId;
    private String currencyName;
    private EmailAlertType emailAlertType;
    private BigDecimal percentageChange;
    private BigDecimal targetPrice;
    private boolean active;
    private BigDecimal initialPrice;
}


