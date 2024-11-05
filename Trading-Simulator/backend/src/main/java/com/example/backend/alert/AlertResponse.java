package com.example.backend.alert;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AlertResponse {
    private Integer alertId;
    private Integer currencyId;
    private String currencyName;
    private AlertType alertType;
    private BigDecimal percentageChange;
    private BigDecimal targetPrice;
    private boolean active;
}
