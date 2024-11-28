package com.example.backend.alert.mail;

import com.example.backend.userEvent.UserEventTrackingService;
import com.example.backend.userEvent.UserEvent;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.exceptions.*;
import com.example.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailAlertService {

    private final EmailAlertRepository emailAlertRepository;
    private final CurrencyRepository currencyRepository;
    private final AuthenticationService authenticationService;
    private final UserEventTrackingService userEventTrackingService;

    @Transactional
    public EmailAlertResponse createAlert(CreateEmailAlertRequest request) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        Currency currency = currencyRepository.findById(request.getCurrencyid())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));

        if (request.getEmailAlertType() == EmailAlertType.PERCENTAGE) {
            if (request.getPercentageChange() == null || request.getPercentageChange().compareTo(BigDecimal.ZERO) == 0) {
                throw new InvalidAlertParametersException("Percentage change must be provided and non-zero for percentage alerts");
            }
        } else if (request.getEmailAlertType() == EmailAlertType.PRICE) {
            if (request.getTargetPrice() == null || request.getTargetPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidAlertParametersException("Target price must be provided and greater than zero for price alerts");
            }
        } else {
            throw new UnsupportedAlertTypeException("Unsupported alert type");
        }

        BigDecimal currentPrice = currency.getCurrentPrice();
        if (currentPrice == null) {
            throw new PriceNotAvailableException("Current price is not available for the selected currency");
        }

        EmailAlert emailAlert = EmailAlert.builder()
                .user(user)
                .currency(currency)
                .emailAlertType(request.getEmailAlertType())
                .percentageChange(request.getPercentageChange())
                .targetPrice(request.getTargetPrice())
                .active(true)
                .initialPrice(currentPrice)
                .build();

        emailAlertRepository.save(emailAlert);

        try {
            Map<String, Object> details = Map.of(
                    "alertId", emailAlert.getAlertid(),
                    "currencyId", currency.getCurrencyid(),
                    "currencySymbol", currency.getSymbol(),
                    "emailAlertType", emailAlert.getEmailAlertType().toString(),
                    "percentageChange", emailAlert.getPercentageChange() != null ? emailAlert.getPercentageChange() : "N/A",
                    "targetPrice", emailAlert.getTargetPrice() != null ? emailAlert.getTargetPrice() : "N/A"
            );
            userEventTrackingService.logEvent(email, UserEvent.EventType.CREATE_EMAIL_ALERT, details);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return EmailAlertResponse.builder()
                .alertId(emailAlert.getAlertid())
                .currencyId(currency.getCurrencyid())
                .currencyName(currency.getName())
                .emailAlertType(emailAlert.getEmailAlertType())
                .percentageChange(emailAlert.getPercentageChange())
                .targetPrice(emailAlert.getTargetPrice())
                .active(emailAlert.isActive())
                .build();
    }

    public List<EmailAlertResponse> getUserAlerts() {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        return emailAlertRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateAlert(Integer alertId) {
        EmailAlert emailAlert = emailAlertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        emailAlert.setActive(false);
        emailAlertRepository.save(emailAlert);
    }

    private EmailAlertResponse mapToResponse(EmailAlert emailAlert) {
        return EmailAlertResponse.builder()
                .alertId(emailAlert.getAlertid())
                .currencyId(emailAlert.getCurrency().getCurrencyid())
                .currencyName(emailAlert.getCurrency().getName())
                .emailAlertType(emailAlert.getEmailAlertType())
                .percentageChange(emailAlert.getPercentageChange())
                .targetPrice(emailAlert.getTargetPrice())
                .active(emailAlert.isActive())
                .initialPrice(emailAlert.getInitialPrice())
                .build();
    }


    @Transactional
    public void deleteAlert(Integer alertId) {
        EmailAlert emailAlert = emailAlertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);
        if (!emailAlert.getUser().equals(user)) {
            throw new UnauthorizedActionException("You do not have permission to delete this alert");
        }

        Map<String, Object> details = Map.of(
                "alertId", alertId,
                "currencyId", emailAlert.getCurrency().getCurrencyid(),
                "currencySymbol", emailAlert.getCurrency().getSymbol(),
                "emailAlertType", emailAlert.getEmailAlertType().toString()
        );
        userEventTrackingService.logEvent(email, UserEvent.EventType.DELETE_NOTIFICATION, details);

        emailAlertRepository.delete(emailAlert);
    }
}
