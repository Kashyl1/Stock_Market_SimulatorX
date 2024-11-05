package com.example.backend.alert;

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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final CurrencyRepository currencyRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public AlertResponse createAlert(CreateAlertRequest request) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        Currency currency = currencyRepository.findById(request.getCurrencyid())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));

        if (request.getAlertType() == AlertType.PERCENTAGE) {
            if (request.getPercentageChange() == null || request.getPercentageChange().compareTo(BigDecimal.ZERO) == 0) {
                throw new InvalidAlertParametersException("Percentage change must be provided and non-zero for percentage alerts");
            }
        } else if (request.getAlertType() == AlertType.PRICE) {
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

        Alert alert = Alert.builder()
                .user(user)
                .currency(currency)
                .alertType(request.getAlertType())
                .percentageChange(request.getPercentageChange())
                .targetPrice(request.getTargetPrice())
                .active(true)
                .initialPrice(currentPrice)
                .build();

        alertRepository.save(alert);

        return AlertResponse.builder()
                .alertId(alert.getAlertId())
                .currencyId(currency.getCurrencyid())
                .currencyName(currency.getName())
                .alertType(alert.getAlertType())
                .percentageChange(alert.getPercentageChange())
                .targetPrice(alert.getTargetPrice())
                .active(alert.isActive())
                .build();
    }

    public List<AlertResponse> getUserAlerts() {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        return alertRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deactivateAlert(Integer alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);
        if (!alert.getUser().equals(user)) {
            throw new UnauthorizedActionException("You do not have permission to modify this alert");
        }

        alert.setActive(false);
        alertRepository.save(alert);
    }

    private AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .alertId(alert.getAlertId())
                .currencyId(alert.getCurrency().getCurrencyid())
                .currencyName(alert.getCurrency().getName())
                .alertType(alert.getAlertType())
                .percentageChange(alert.getPercentageChange())
                .targetPrice(alert.getTargetPrice())
                .active(alert.isActive())
                .build();
    }

    @Transactional
    public void deleteAlert(Integer alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException("Alert not found"));

        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);
        if (!alert.getUser().equals(user)) {
            throw new UnauthorizedActionException("You do not have permission to delete this alert");
        }

        alertRepository.delete(alert);
    }
}
