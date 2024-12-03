package com.example.backend.alert.trade;

import com.example.backend.mailVerification.VerificationService;
import com.example.backend.userEvent.UserEventTrackingService;
import com.example.backend.userEvent.UserEvent;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.exceptions.*;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAsset;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.transaction.Transaction;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeAlertService {

    private final TradeAlertRepository tradeAlertRepository;
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final CurrencyRepository currencyRepository;
    private final AuthenticationService authenticationService;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final TransactionRepository transactionRepository;
    private final VerificationService verificationService;
    private final UserEventTrackingService userEventTrackingService;
    private static final Logger logger = LoggerFactory.getLogger(TradeAlertService.class);


    @Transactional
    public TradeAlert createTradeAlert(CreateTradeAlertRequest request) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

        Currency currency = currencyRepository.findById(request.getCurrencyId())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));

        if (request.getTradeAlertType() == TradeAlertType.SELL) {
            PortfolioAsset asset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                    .orElseThrow(() -> new AssetNotOwnedException("You do not own this currency in the selected portfolio"));

            if (asset.getAmount().compareTo(request.getTradeAmount()) < 0) {
                throw new InsufficientAssetAmountException("You do not have enough of this currency to set this trade alert");
            }
        }

        if (request.getTradeAlertType() == TradeAlertType.BUY) {
            BigDecimal amountInUSD = request.getTradeAmount();
            if (user.getBalance().compareTo(amountInUSD) < 0) {
                throw new InsufficientFundsException("Insufficient balance to set this trade alert");
            }

            user.setBalance(user.getBalance().subtract(amountInUSD));
            user.setReservedBalance(user.getReservedBalance().add(amountInUSD));
            userRepository.save(user);
        }

        BigDecimal currentPrice = currency.getCurrentPrice();
        if (currentPrice == null) {
            throw new PriceNotAvailableException("Current price is not available for the selected currency");
        }

        TradeAlert tradeAlert = TradeAlert.builder()
                .user(user)
                .portfolio(portfolio)
                .currency(currency)
                .tradeAlertType(request.getTradeAlertType())
                .conditionPrice(request.getConditionPrice())
                .tradeAmount(request.getTradeAmount())
                .orderType(request.getOrderType())
                .active(true)
                .initialPrice(currentPrice)
                .build();

        TradeAlert savedTradeAlert = tradeAlertRepository.save(tradeAlert);

        try {
            Map<String, Object> details = Map.of(
                    "tradeAlertId", savedTradeAlert.getTradeAlertid(),
                    "portfolioId", portfolio.getPortfolioid(),
                    "currencyId", currency.getCurrencyid(),
                    "currencySymbol", currency.getSymbol(),
                    "tradeAlertType", tradeAlert.getTradeAlertType().toString(),
                    "conditionPrice", tradeAlert.getConditionPrice(),
                    "tradeAmount", tradeAlert.getTradeAmount() != null ? tradeAlert.getTradeAmount() : "N/A"
            );
            userEventTrackingService.logEvent(email, UserEvent.EventType.CREATE_TRADE_ALERT, details);
        } catch (Exception e) {
            logger.error("Error while logging user event: {}", e.getMessage(), e);
        }


        return savedTradeAlert;
    }

    @Transactional
    public void deactivateTradeAlert(Integer tradeAlertId) {
        TradeAlert tradeAlert = tradeAlertRepository.findById(tradeAlertId)
                .orElseThrow(() -> new UnsupportedAlertTypeException("Trade alert not found"));

        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (tradeAlert.getTradeAlertType() == TradeAlertType.BUY && tradeAlert.isActive()) {
            BigDecimal tradeAmount = tradeAlert.getTradeAmount();
            user.setReservedBalance(user.getReservedBalance().subtract(tradeAmount));
            user.setBalance(user.getBalance().add(tradeAmount));
            userRepository.save(user);
        }
        tradeAlert.setActive(false);
        tradeAlertRepository.save(tradeAlert);
    }

    @Transactional
    public void deleteTradeAlert(Integer tradeAlertId) {
        TradeAlert tradeAlert = tradeAlertRepository.findById(tradeAlertId)
                .orElseThrow(() -> new UnsupportedAlertTypeException("Trade alert not found"));

        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (tradeAlert.getTradeAlertType() == TradeAlertType.BUY && tradeAlert.isActive()) {
            BigDecimal tradeAmount = tradeAlert.getTradeAmount();
            user.setReservedBalance(user.getReservedBalance().subtract(tradeAmount));
            user.setBalance(user.getBalance().add(tradeAmount));
            userRepository.save(user);
        }

        try {
            Map<String, Object> details = Map.of(
                    "tradeAlertId", tradeAlertId,
                    "portfolioId", tradeAlert.getPortfolio().getPortfolioid(),
                    "currencyId", tradeAlert.getCurrency().getCurrencyid(),
                    "currencySymbol", tradeAlert.getCurrency().getSymbol(),
                    "tradeAlertType", tradeAlert.getTradeAlertType() != null ? tradeAlert.getTradeAlertType().toString() : "N/A"
            );
            userEventTrackingService.logEvent(email, UserEvent.EventType.DELETE_NOTIFICATION, details);
        } catch (Exception e) {
            logger.error("Error while logging user event: {}", e.getMessage(), e);
        }

        tradeAlertRepository.delete(tradeAlert);
    }

    @Transactional(readOnly = true)
    public List<TradeAlert> getUserTradeAlerts() {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);
        return tradeAlertRepository.findByUser(user);
    }

    @Transactional
    public void executeBuyFromReserved(TradeAlert tradeAlert) {
        TradeAlertType alertType = tradeAlert.getTradeAlertType();
        BigDecimal tradeAmount = tradeAlert.getTradeAmount();
        User user = tradeAlert.getUser();

        if (alertType != TradeAlertType.BUY) {
            throw new IllegalArgumentException("This method only handles BUY alerts.");
        }

        user.setReservedBalance(user.getReservedBalance().subtract(tradeAmount));
        userRepository.save(user);

        buyAssetForAlert(tradeAlert.getPortfolio().getPortfolioid(),
                tradeAlert.getCurrency().getSymbol(),
                tradeAmount,
                user);

        verificationService.sendTradeExecutedEmail(user, tradeAlert.getCurrency(), tradeAmount, alertType);
    }

    @Transactional
    public void executeSell(TradeAlert tradeAlert) {
        TradeAlertType alertType = tradeAlert.getTradeAlertType();
        BigDecimal tradeAmount = tradeAlert.getTradeAmount();
        User user = tradeAlert.getUser();
        Portfolio portfolio = tradeAlert.getPortfolio();
        Currency currency = tradeAlert.getCurrency();

        if (alertType != TradeAlertType.SELL) {
            throw new IllegalArgumentException("This method only handles SELL alerts.");
        }

        PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                .orElse(null);

        if (portfolioAsset == null || portfolioAsset.getAmount().compareTo(tradeAmount) < 0) {
            tradeAlert.setActive(false);
            tradeAlertRepository.save(tradeAlert);
            return;
        }

        sellAssetForAlert(portfolio.getPortfolioid(), currency.getCurrencyid(), tradeAmount, user);

        verificationService.sendTradeExecutedEmail(user, currency, tradeAmount, alertType);


    }

    private void buyAssetForAlert(Integer portfolioid, String currencySymbol, BigDecimal amountInUSD, User user) {

        if (amountInUSD != null) {
            Currency currency = currencyRepository.findBySymbol(currencySymbol.toUpperCase())
                    .orElseThrow(() -> new CurrencyNotFoundException("Currency not found in database"));

            Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                    .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

            BigDecimal rate = currency.getCurrentPrice();
            if (rate == null) {
                throw new PriceNotAvailableException("Current price not available for " + currencySymbol);
            }

            BigDecimal amountOfCurrencyCalculated = amountInUSD.divide(rate, 8, RoundingMode.HALF_UP);

            PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                    .orElse(PortfolioAsset.builder()
                            .portfolio(portfolio)
                            .currency(currency)
                            .amount(BigDecimal.ZERO)
                            .averagePurchasePrice(BigDecimal.ZERO)
                            .currentPrice(rate)
                            .updatedAt(LocalDateTime.now())
                            .build());

            BigDecimal totalAmount = portfolioAsset.getAmount().add(amountOfCurrencyCalculated);
            BigDecimal totalCost = portfolioAsset.getAmount().multiply(portfolioAsset.getAveragePurchasePrice())
                    .add(amountInUSD);

            BigDecimal newAveragePrice = totalCost.divide(totalAmount, 8, RoundingMode.HALF_UP);

            portfolioAsset.setAmount(totalAmount);
            portfolioAsset.setAveragePurchasePrice(newAveragePrice);
            portfolioAsset.setCurrentPrice(rate);
            portfolioAsset.setUpdatedAt(LocalDateTime.now());

            portfolio.setUpdatedAt(LocalDateTime.now());
            portfolioRepository.save(portfolio);
            portfolioAssetRepository.save(portfolioAsset);

            Transaction transaction = Transaction.builder()
                    .currency(currency)
                    .transactionType("BUY")
                    .amount(amountOfCurrencyCalculated)
                    .rate(rate)
                    .timestamp(LocalDateTime.now())
                    .user(user)
                    .portfolio(portfolio)
                    .build();

            transactionRepository.save(transaction);
        }
    }

    private void sellAssetForAlert(Integer portfolioid, Integer currencyid, BigDecimal amountOfCurrency, User user) {
        if (amountOfCurrency != null) {
            Currency currency = currencyRepository.findById(currencyid)
                    .orElseThrow(() -> new CurrencyNotFoundException("Currency not found in database"));

            Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                    .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

            BigDecimal rate = currency.getCurrentPrice();
            if (rate == null) {
                throw new PriceNotAvailableException("Current price not available for currency ID: " + currencyid);
            }

            BigDecimal amountInUSDCalculated = amountOfCurrency.multiply(rate).setScale(8, RoundingMode.HALF_UP);

            PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                    .orElseThrow(() -> new AssetNotOwnedException("You do not own this currency"));

            BigDecimal newAmount = portfolioAsset.getAmount().subtract(amountOfCurrency).setScale(8, RoundingMode.HALF_UP);
            portfolioAsset.setAmount(newAmount);
            portfolioAsset.setUpdatedAt(LocalDateTime.now());
            portfolio.setUpdatedAt(LocalDateTime.now());
            portfolioRepository.save(portfolio);

            if (newAmount.compareTo(BigDecimal.ZERO) == 0) {
                portfolioAssetRepository.delete(portfolioAsset);
            } else {
                portfolioAssetRepository.save(portfolioAsset);
            }
            BigDecimal newBalance = user.getBalance().add(amountInUSDCalculated).setScale(8, RoundingMode.HALF_UP);
            user.setBalance(newBalance);
            userRepository.save(user);

            Transaction transaction = Transaction.builder()
                    .currency(currency)
                    .transactionType("SELL")
                    .amount(amountOfCurrency.setScale(8, RoundingMode.HALF_UP))
                    .rate(rate.setScale(8, RoundingMode.HALF_UP))
                    .timestamp(LocalDateTime.now())
                    .user(user)
                    .portfolio(portfolio)
                    .build();

            transactionRepository.save(transaction);
        }
    }
}
