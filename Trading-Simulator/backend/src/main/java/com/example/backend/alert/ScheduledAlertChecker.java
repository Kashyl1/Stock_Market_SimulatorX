package com.example.backend.alert;

import com.example.backend.alert.mail.EmailAlert;
import com.example.backend.alert.mail.EmailAlertRepository;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.exceptions.EmailSendingException;
import com.example.backend.transaction.TransactionService;
import com.example.backend.user.User;
import com.example.backend.MailVerification.VerificationService;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.example.backend.alert.trade.*;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAsset;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledAlertChecker {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledAlertChecker.class);
    private final EmailAlertRepository emailAlertRepository;
    private final VerificationService verificationService;
    private final TradeAlertRepository tradeAlertRepository;
    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final TradeAlertService tradeAlertService;

    @Scheduled(fixedRate = 1000 * 45)
    public void checkAllAlerts() {
        checkEmailAlerts();
        checkTradeAlerts();
    }

    @Transactional
    public void checkEmailAlerts() {
        List<EmailAlert> activeEmailAlerts = emailAlertRepository.findByActiveTrue();

        for (EmailAlert emailAlert : activeEmailAlerts) {
            Currency currency = emailAlert.getCurrency();
            BigDecimal currentPrice = currency.getCurrentPrice();

            if (currentPrice == null) {
                continue;
            }

            boolean shouldTrigger = false;

            switch (emailAlert.getEmailAlertType()) {
                case PERCENTAGE:
                    BigDecimal initialPrice = emailAlert.getInitialPrice();
                    BigDecimal percentageChange = emailAlert.getPercentageChange();

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
                    BigDecimal targetPrice = emailAlert.getTargetPrice();
                    if (currentPrice.compareTo(targetPrice) >= 0) {
                        shouldTrigger = true;
                    }
                    break;

                default:
                    continue;
            }

            if (shouldTrigger) {
                User user = emailAlert.getUser();
                try {
                    verificationService.sendAlertEmail(user, currency, currentPrice, emailAlert);
                    logger.info("Alert triggered for user {}.", user.getEmail());
                } catch (EmailSendingException e) {
                    logger.error("Failed to send alert email to user {}: {}", user.getEmail(), e.getMessage());
                    continue;
                } catch (Exception e) {
                    logger.error("Unexpected error while processing email alert for user {}: {}", user.getEmail(), e.getMessage());
                    continue;
                }

                emailAlert.setActive(false);
                emailAlertRepository.save(emailAlert);
            }
        }
    }

    @Transactional
    public void checkTradeAlerts() {
        List<TradeAlert> activeTradeAlerts = tradeAlertRepository.findByActiveTrue();

        for (TradeAlert tradeAlert : activeTradeAlerts) {
            Currency currency = tradeAlert.getCurrency();
            BigDecimal currentPrice = currency.getCurrentPrice();

            if (currentPrice == null) {
                continue;
            }

            boolean shouldTrigger = false;

            switch (tradeAlert.getConditionType()) {
                case PERCENTAGE:

                    Portfolio portfolio = tradeAlert.getPortfolio();
                    User user = tradeAlert.getUser();

                    PortfolioAsset portfolioAsset = portfolio.getPortfolioAssets().stream()
                            .filter(asset -> asset.getCurrency().equals(currency))
                            .findFirst()
                            .orElse(null);

                    if (portfolioAsset == null) {
                        continue;
                    }

                    BigDecimal averagePurchasePrice = portfolioAsset.getAveragePurchasePrice();
                    BigDecimal percentageChange = tradeAlert.getConditionValue();

                    BigDecimal targetPriceValue = averagePurchasePrice.multiply(
                            BigDecimal.ONE.add(percentageChange.divide(BigDecimal.valueOf(100)))
                    );

                    if (tradeAlert.getTradeAlertType() == TradeAlertType.SELL) {
                        if (currentPrice.compareTo(targetPriceValue) >= 0) {
                            shouldTrigger = true;
                        }
                    } else if (tradeAlert.getTradeAlertType() == TradeAlertType.BUY) {
                        BigDecimal targetBuyPrice = averagePurchasePrice.multiply(
                                BigDecimal.ONE.subtract(percentageChange.divide(BigDecimal.valueOf(100)))
                        );
                        if (currentPrice.compareTo(targetBuyPrice) <= 0) {
                            shouldTrigger = true;
                        }
                    }
                    break;

                case PRICE:
                    BigDecimal conditionPrice = tradeAlert.getConditionValue();

                    if (tradeAlert.getTradeAlertType() == TradeAlertType.BUY) {
                        if (currentPrice.compareTo(conditionPrice) <= 0) {
                            shouldTrigger = true;
                        }
                    } else if (tradeAlert.getTradeAlertType() == TradeAlertType.SELL) {
                        if (currentPrice.compareTo(conditionPrice) >= 0) {
                            shouldTrigger = true;
                        }
                    }
                    break;
            }

            if (shouldTrigger) {
                try {
                    if (tradeAlert.getTradeAlertType() == TradeAlertType.BUY) {
                        tradeAlertService.executeBuyFromReserved(tradeAlert, currentPrice);
                    } else if (tradeAlert.getTradeAlertType() == TradeAlertType.SELL) {
                        tradeAlertService.executeSellFromReserved(tradeAlert, currentPrice);
                    }
                    tradeAlert.setActive(false);
                    tradeAlertRepository.save(tradeAlert);
                } catch (Exception e) {
                    logger.error("Failed to execute trade alert {}: {}", tradeAlert.getTradeAlertId(), e.getMessage());
                    continue;
                }
            }
        }
    }
}
