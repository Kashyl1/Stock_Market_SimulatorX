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

    private EmailAlertResponse mapToResponse(EmailAlert emailAlert) {
        return EmailAlertResponse.builder()
                .alertId(emailAlert.getAlertId())
                .currencyId(emailAlert.getCurrency().getCurrencyid())
                .currencyName(emailAlert.getCurrency().getName())
                .emailAlertType(emailAlert.getEmailAlertType())
                .percentageChange(emailAlert.getPercentageChange())
                .targetPrice(emailAlert.getTargetPrice())
                .active(emailAlert.isActive())
                .build();
    }
}


