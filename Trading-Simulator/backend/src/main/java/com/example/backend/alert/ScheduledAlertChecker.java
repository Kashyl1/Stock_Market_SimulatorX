package com.example.backend.alert;

import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.exceptions.EmailSendingException;
import com.example.backend.user.User;
import com.example.backend.MailVerification.VerificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledAlertChecker {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledAlertChecker.class);
    private final AlertRepository alertRepository;
    private final CurrencyRepository currencyRepository;
    private final VerificationService verificationService;

    @Scheduled(fixedRate = 1000 * 60 * 2)
    @Transactional
    public void checkAlerts() {
        logger.info("Checking price alerts...");

        List<Alert> activeAlerts = alertRepository.findByActiveTrue();

        for (Alert alert : activeAlerts) {
            Currency currency = alert.getCurrency();
            BigDecimal currentPrice = currency.getCurrentPrice();

            if (currentPrice == null) {
                logger.warn("No current price available for currency: {}", currency.getName());
                continue;
            }

            boolean shouldTrigger = false;

            switch (alert.getAlertType()) {
                case PERCENTAGE:
                    BigDecimal initialPrice = alert.getInitialPrice();
                    BigDecimal percentageChange = alert.getPercentageChange();

                    BigDecimal expectedPriceChange = initialPrice.multiply(percentageChange.abs()).divide(BigDecimal.valueOf(100));
                    BigDecimal targetPriceIncrease = initialPrice.add(expectedPriceChange);
                    BigDecimal targetPriceDecrease = initialPrice.subtract(expectedPriceChange);

                    if (percentageChange.compareTo(BigDecimal.ZERO) > 0) {
                        if (currentPrice.compareTo(targetPriceIncrease) >= 0) {
                            shouldTrigger = true;
                        }
                    } else {
                        if (currentPrice.compareTo(targetPriceDecrease) <= 0) {
                            shouldTrigger = true;
                        }
                    }
                    break;

                case PRICE:
                    BigDecimal targetPrice = alert.getTargetPrice();
                    if (currentPrice.compareTo(targetPrice) >= 0) {
                        shouldTrigger = true;
                    }
                    break;

                default:
                    logger.warn("Unsupported alert type for alert ID: {}", alert.getAlertId());
                    continue;
            }

            if (shouldTrigger) {
                User user = alert.getUser();
                try {
                    verificationService.sendAlertEmail(user, currency, currentPrice, alert);
                    logger.info("Alert triggered for user {}.", user.getEmail());
                } catch (EmailSendingException e) {
                    logger.error("Error sending email notification to user {}: {}", user.getEmail(), e.getMessage());
                    continue;
                } catch (Exception e) {
                    logger.error("Unexpected error during alert processing for user {}: {}", user.getEmail(), e.getMessage());
                    continue;
                }

                alert.setActive(false);
                alertRepository.save(alert);
            }
        }

        logger.info("Price alerts checking completed.");
    }
}
