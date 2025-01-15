package com.example.backend.transaction;

import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.exceptions.*;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAsset;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.example.backend.userEvent.UserEvent;
import com.example.backend.userEvent.UserEventTrackingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionOperationService {

    private final PortfolioRepository portfolioRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final TransactionRepository transactionRepository;
    private final UserEventTrackingService userEventTrackingService;

    @Transactional
    @Operation(summary = "Buy an asset", description = "Processes the purchase of an asset")
    public void buyAsset(Integer portfolioid, String currencySymbol, BigDecimal amountInUSD, BigDecimal amountOfCurrency, User user) {
        Pair<BigDecimal, BigDecimal> validatedAmounts = validateInput(amountInUSD, amountOfCurrency);
        BigDecimal finalAmountInUsd = validatedAmounts.getLeft();
        BigDecimal finalAmountOfCurrency = validatedAmounts.getRight();

        Portfolio portfolio = getPortfolioOrThrow(portfolioid, user);
        Currency currency = getCurrencyOrThrow(currencySymbol);

        if (finalAmountInUsd != null) {
            buyAssetByUSD(finalAmountInUsd, currency, portfolio, user);
        } else {
            buyAssetByCurrency(finalAmountOfCurrency, currency, portfolio, user);
        }

    }

    @Transactional
    @Operation(summary = "Sell an asset", description = "Processes the sale of an asset")
    public void sellAsset(Integer portfolioid, Integer currencyid, BigDecimal amountOfCurrency, BigDecimal priceInUSD, User user) {
        Pair<BigDecimal, BigDecimal> validatedAmounts = validateInput(priceInUSD, amountOfCurrency);
        BigDecimal finalAmountOfCurrency = validatedAmounts.getRight();
        BigDecimal finalPriceInUSD = validatedAmounts.getLeft();

        Portfolio portfolio = getPortfolioOrThrow(portfolioid, user);
        Currency currency = getCurrencyByIdOrThrow(currencyid);

        if (finalAmountOfCurrency != null) {
            sellByCurrencyAmount(finalAmountOfCurrency, currency, portfolio, user);
        } else {
            sellByUSDValue(finalPriceInUSD, currency, portfolio, user);
        }
    }

    private Pair<BigDecimal, BigDecimal> validateInput(BigDecimal amountInUsd, BigDecimal amountOfCurrency) {
        if (amountInUsd != null && amountOfCurrency != null) {
            throw new IllegalArgumentException("Please provide either amount in USD or amount of currency, not both");
        }
        if (amountInUsd == null && amountOfCurrency == null) {
            throw new IllegalArgumentException("Either amount in usd or amount of currency must be provided");
        }

        if (amountInUsd != null && amountInUsd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount in USD must be positive");
        }
        if (amountOfCurrency != null && amountOfCurrency.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount of currency must be positive!");
        }

        return Pair.of(amountInUsd, amountOfCurrency);
    }

    private Portfolio getPortfolioOrThrow(Integer portfolioid, User user) {
        return portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
    }

    private Currency getCurrencyOrThrow(String currencySymbol) {
        Currency currency = currencyRepository.findBySymbol(currencySymbol.toUpperCase())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found"));
        if (currency.getCurrentPrice() == null) {
            throw new PriceNotAvailableException("Current price not available for " + currencySymbol);
        }
        return currency;
    }

    private Currency getCurrencyByIdOrThrow(Integer currencyid) {
        return currencyRepository.findById(currencyid)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found in database"));
    }

    private PortfolioAsset getPortfolioAssetOrThrow(Portfolio portfolio, Currency currency) {
        return portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                .orElseThrow(() -> new AssetNotOwnedException("You do not own this currency"));
    }


    private void buyAssetByUSD(BigDecimal finalAmountInUSD,
                               Currency currency,
                               Portfolio portfolio,
                               User user) {
        BigDecimal rate = currency.getCurrentPrice();
        BigDecimal amountOfCurrencyCalculated = finalAmountInUSD.divide(rate, 8, RoundingMode.HALF_UP);
        if (user.getBalance().compareTo(finalAmountInUSD) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(finalAmountInUSD));
        userRepository.save(user);

        PortfolioAsset portfolioAsset = portfolioAssetRepository
                .findByPortfolioAndCurrency(portfolio, currency)
                .orElse(PortfolioAsset.builder()
                        .portfolio(portfolio)
                        .currency(currency)
                        .amount(BigDecimal.ZERO)
                        .averagePurchasePrice(BigDecimal.ZERO)
                        .updatedAt(LocalDateTime.now())
                        .build());

        BigDecimal totalAmount = portfolioAsset.getAmount().add(amountOfCurrencyCalculated);
        BigDecimal totalCost = portfolioAsset.getAmount().multiply(portfolioAsset.getAveragePurchasePrice())
                .add(finalAmountInUSD);

        BigDecimal newAveragePrice = totalCost.divide(totalAmount, 8, RoundingMode.HALF_UP);

        portfolioAsset.setAmount(totalAmount);
        portfolioAsset.setAveragePurchasePrice(newAveragePrice);
        portfolioAsset.setUpdatedAt(LocalDateTime.now());

        portfolio.setUpdatedAt(LocalDateTime.now());
        portfolioRepository.save(portfolio);
        portfolioAssetRepository.save(portfolioAsset);

        logTransaction("BUY", currency, portfolio, user, amountOfCurrencyCalculated, rate);
        logTradeEvent("BUY", portfolio, user, currency, finalAmountInUSD, amountOfCurrencyCalculated, rate);
    }

    private void buyAssetByCurrency(BigDecimal finalAmountOfCurrency,
                                    Currency currency,
                                    Portfolio portfolio,
                                    User user) {
        BigDecimal rate = currency.getCurrentPrice();

        BigDecimal amountInUSDCalculated = finalAmountOfCurrency.multiply(rate)
                .setScale(8, RoundingMode.HALF_UP);

        if (user.getBalance().compareTo(amountInUSDCalculated) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(amountInUSDCalculated));
        userRepository.save(user);

        PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                .orElse(PortfolioAsset.builder()
                        .portfolio(portfolio)
                        .currency(currency)
                        .amount(BigDecimal.ZERO)
                        .averagePurchasePrice(BigDecimal.ZERO)
                        .updatedAt(LocalDateTime.now())
                        .build());

        BigDecimal totalAmount = portfolioAsset.getAmount().add(finalAmountOfCurrency);

        BigDecimal totalCost = portfolioAsset.getAmount()
                .multiply(portfolioAsset.getAveragePurchasePrice())
                .add(amountInUSDCalculated);

        BigDecimal newAveragePrice = totalCost.divide(totalAmount, 8, RoundingMode.HALF_UP);

        portfolioAsset.setAmount(totalAmount);
        portfolioAsset.setAveragePurchasePrice(newAveragePrice);
        portfolioAsset.setUpdatedAt(LocalDateTime.now());

        portfolio.setUpdatedAt(LocalDateTime.now());
        portfolioRepository.save(portfolio);
        portfolioAssetRepository.save(portfolioAsset);

        logTransaction("BUY", currency, portfolio, user, amountInUSDCalculated, rate);
        logTradeEvent("BUY", portfolio, user, currency, amountInUSDCalculated, finalAmountOfCurrency, rate);
    }

    private void logTradeEvent(String tradeType,
                               Portfolio portfolio,
                               User user,
                               Currency currency,
                               BigDecimal usdAmount,
                               BigDecimal currencyAmount,
                               BigDecimal rate) {

        Map<String, Object> details = new HashMap<>();
        details.put("portfolioId", portfolio.getPortfolioid());
        details.put("currencySymbol", currency.getSymbol());
        details.put("amountInUSD", usdAmount);
        details.put("amountOfCurrency", currencyAmount);
        details.put("rate", rate);
        details.put("currencyId", currency.getCurrencyid());

        UserEvent.EventType eventType = ("BUY".equalsIgnoreCase(tradeType))
                ? UserEvent.EventType.BUY_CRYPTO
                : UserEvent.EventType.SELL_CRYPTO;

        userEventTrackingService.logEvent(user.getEmail(), eventType, details);
    }


    private void sellByCurrencyAmount(BigDecimal amountOfCurrency,
                                      Currency currency,
                                      Portfolio portfolio,
                                      User user) {
        BigDecimal rate = currency.getCurrentPrice();
        if (rate == null) {
            throw new PriceNotAvailableException("Current price not available for currency ID: " + currency.getCurrencyid());
        }

        BigDecimal amountInUSD = amountOfCurrency.multiply(rate).setScale(8, RoundingMode.HALF_UP);

        PortfolioAsset portfolioAsset = getPortfolioAssetOrThrow(portfolio, currency);

        if (portfolioAsset.getAmount().compareTo(amountOfCurrency) < 0) {
            throw new InsufficientAssetAmountException("Insufficient amount of currency to sell");
        }

        updatePortfolioAssetAfterSell(portfolioAsset, amountOfCurrency);

        updateUserBalanceAfterSell(user, amountInUSD);

        logTransaction("SELL", currency, portfolio, user, amountOfCurrency, rate);
        logTradeEvent("SELL", portfolio, user, currency, amountInUSD, amountOfCurrency, rate);
    }

    private void sellByUSDValue(BigDecimal priceInUSD,
                                Currency currency,
                                Portfolio portfolio,
                                User user) {
        BigDecimal rate = currency.getCurrentPrice();
        if (rate == null) {
            throw new PriceNotAvailableException("Current price not available for currency ID: " + currency.getCurrencyid());
        }

        BigDecimal amountOfCurrency = priceInUSD.divide(rate, 8, RoundingMode.HALF_UP);

        PortfolioAsset portfolioAsset = getPortfolioAssetOrThrow(portfolio, currency);

        if (portfolioAsset.getAmount().compareTo(amountOfCurrency) < 0) {
            throw new InsufficientAssetAmountException("Insufficient amount of currency to sell");
        }

        updatePortfolioAssetAfterSell(portfolioAsset, amountOfCurrency);
        updateUserBalanceAfterSell(user, priceInUSD);
        logTransaction("SELL", currency, portfolio, user, amountOfCurrency, rate);
        logTradeEvent("SELL", portfolio, user, currency, priceInUSD, amountOfCurrency, rate);
    }

    private void updatePortfolioAssetAfterSell(PortfolioAsset portfolioAsset, BigDecimal amountOfCurrency) {
        BigDecimal newAmount = portfolioAsset.getAmount().subtract(amountOfCurrency).setScale(8, RoundingMode.HALF_UP);
            portfolioAsset.setAmount(newAmount);
            portfolioAsset.setUpdatedAt(LocalDateTime.now());
            portfolioAssetRepository.save(portfolioAsset);

    }

    private void updateUserBalanceAfterSell(User user, BigDecimal amountInUSD) {
        BigDecimal newBalance = user.getBalance().add(amountInUSD).setScale(8, RoundingMode.HALF_UP);
        user.setBalance(newBalance);
        userRepository.save(user);
    }

    private void logTransaction(String type,
                                Currency currency,
                                Portfolio portfolio,
                                User user,
                                BigDecimal amount,
                                BigDecimal rate) {
        Transaction transaction = Transaction.builder()
                .currency(currency)
                .transactionType(type)
                .amount(amount.setScale(8, RoundingMode.HALF_UP))
                .rate(rate.setScale(8, RoundingMode.HALF_UP))
                .timestamp(LocalDateTime.now())
                .user(user)
                .portfolio(portfolio)
                .build();
        transactionRepository.save(transaction);
    }
}
