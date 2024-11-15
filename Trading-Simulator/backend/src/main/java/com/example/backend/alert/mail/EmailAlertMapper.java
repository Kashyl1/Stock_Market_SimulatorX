package com.example.backend.alert.mail;

import org.springframework.stereotype.Component;

@Component
public class EmailAlertMapper {
    public EmailAlertDTO toDTO(EmailAlert emailAlert) {
        return EmailAlertDTO.builder()
                .emailAlertid(emailAlert.getAlertid())
                .userid(emailAlert.getUser().getId())
                .userEmail(emailAlert.getUser().getEmail())
                .currencyid(emailAlert.getCurrency().getCurrencyid())
                .currencySymbol(emailAlert.getCurrency().getSymbol())
                .emailAlertType(emailAlert.getEmailAlertType())
                .initialPrice(emailAlert.getInitialPrice())
                .targetPrice(emailAlert.getTargetPrice())
                .percentageChange(emailAlert.getPercentageChange())
                .active(emailAlert.isActive())
                .build();
    }
}
