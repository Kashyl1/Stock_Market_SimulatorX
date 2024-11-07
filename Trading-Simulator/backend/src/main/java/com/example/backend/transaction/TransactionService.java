package com.example.backend.transaction;

import com.example.backend.exceptions.*;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAsset;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final CurrencyRepository currencyRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public Page<Map<String, Object>> getAvailableAssetsWithPrices(Pageable pageable) {
        try {
            Page<Currency> currencies = currencyRepository.findAll(pageable);

            List<Map<String, Object>> assets = currencies.stream()
                    .map(currency -> {
                        Map<String, Object> assetMap = new HashMap<>();
                        assetMap.put("id", currency.getSymbol());
                        assetMap.put("name", currency.getName());
                        assetMap.put("price_in_usd", currency.getCurrentPrice());
                        assetMap.put("price_change_24h", currency.getPriceChange());
                        assetMap.put("price_change_percent_24h", currency.getPriceChangePercent());
                        assetMap.put("volume_24h", currency.getVolume().multiply(currency.getCurrentPrice()));
                        assetMap.put("image_url", currency.getImageUrl());
                        assetMap.put("currencyid", currency.getCurrencyid());
                        return assetMap;
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(assets, pageable, currencies.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get available assets with prices.");
        }
    }


    @Transactional
    public void buyAsset(Integer portfolioid, String currencySymbol, BigDecimal amountInUSD, BigDecimal amountOfCurrency, User user) {

        BigDecimal finalAmountInUSD = null;
        BigDecimal finalAmountOfCurrency = null;

        if (amountInUSD != null && amountOfCurrency != null) {
            throw new IllegalArgumentException("Please provide either amountInUSD or amountOfCurrency, not both.");
        } else if (amountInUSD != null) {
            if (amountInUSD.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount in USD must be positive");
            }
            finalAmountInUSD = amountInUSD;
        } else if (amountOfCurrency != null) {
            if (amountOfCurrency.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount of currency must be positive");
            }
            finalAmountOfCurrency = amountOfCurrency;
        } else {
            throw new IllegalArgumentException("Either amountInUSD or amountOfCurrency must be provided.");
        }

        Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

        Currency currency = currencyRepository.findBySymbol(currencySymbol.toUpperCase())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found in database"));

        BigDecimal rate = currency.getCurrentPrice();
        if (rate == null) {
            throw new PriceNotAvailableException("Current price not available for " + currencySymbol);
        }

        if (finalAmountInUSD != null) {
            BigDecimal amountOfCurrencyCalculated = finalAmountInUSD.divide(rate, 8, BigDecimal.ROUND_HALF_UP);

            if (user.getBalance().compareTo(finalAmountInUSD) < 0) {
                throw new InsufficientFundsException("Insufficient balance");
            }

            user.setBalance(user.getBalance().subtract(finalAmountInUSD));
            userRepository.save(user);

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
                    .add(finalAmountInUSD);

            BigDecimal newAveragePrice = totalAmount.compareTo(BigDecimal.ZERO) == 0
                    ? amountOfCurrencyCalculated
                    : totalCost.divide(totalAmount, 8, BigDecimal.ROUND_HALF_UP);

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

        } else if (finalAmountOfCurrency != null) {
            BigDecimal amountInUSDCalculated = finalAmountOfCurrency.multiply(rate).setScale(8, RoundingMode.HALF_UP);

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
                            .currentPrice(rate)
                            .updatedAt(LocalDateTime.now())
                            .build());

            BigDecimal totalAmount = portfolioAsset.getAmount().add(finalAmountOfCurrency);
            BigDecimal totalCost = portfolioAsset.getAmount().multiply(portfolioAsset.getAveragePurchasePrice())
                    .add(amountInUSDCalculated);

            BigDecimal newAveragePrice = totalAmount.compareTo(BigDecimal.ZERO) == 0
                    ? finalAmountOfCurrency
                    : totalCost.divide(totalAmount, 8, BigDecimal.ROUND_HALF_UP);

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
                    .amount(finalAmountOfCurrency)
                    .rate(rate)
                    .timestamp(LocalDateTime.now())
                    .user(user)
                    .portfolio(portfolio)
                    .build();

            transactionRepository.save(transaction);
        }
    }

    @Transactional
    public void sellAsset(Integer portfolioid, Integer currencyid, BigDecimal amountOfCurrency, BigDecimal priceInUSD, User user) {

        BigDecimal finalAmountOfCurrency = null;
        BigDecimal finalPriceInUSD = null;

        if (amountOfCurrency != null && priceInUSD != null) {
            throw new IllegalArgumentException("Please provide either amountOfCurrency or priceInUSD, not both.");
        } else if (amountOfCurrency != null) {
            if (amountOfCurrency.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount of currency must be positive");
            }
            finalAmountOfCurrency = amountOfCurrency;
        } else if (priceInUSD != null) {
            if (priceInUSD.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price in USD must be positive");
            }
            finalPriceInUSD = priceInUSD;
        } else {
            throw new IllegalArgumentException("Either amountOfCurrency or priceInUSD must be provided.");
        }

        Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

        Currency currency = currencyRepository.findById(currencyid)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found in database"));

        BigDecimal rate = currency.getCurrentPrice();
        if (rate == null) {
            throw new PriceNotAvailableException("Current price not available for currency ID: " + currencyid);
        }


        if (finalAmountOfCurrency != null) {
            BigDecimal amountInUSDCalculated = finalAmountOfCurrency.multiply(rate).setScale(8, RoundingMode.HALF_UP);

            PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                    .orElseThrow(() -> new AssetNotOwnedException("You do not own this currency"));

            if (portfolioAsset.getAmount().compareTo(finalAmountOfCurrency) < 0) {
                throw new InsufficientAssetAmountException("Insufficient amount of currency to sell");
            }

            BigDecimal newAmount = portfolioAsset.getAmount().subtract(finalAmountOfCurrency).setScale(8, RoundingMode.HALF_UP);
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
                    .amount(finalAmountOfCurrency.setScale(8, RoundingMode.HALF_UP))
                    .rate(rate.setScale(8, RoundingMode.HALF_UP))
                    .timestamp(LocalDateTime.now())
                    .user(user)
                    .portfolio(portfolio)
                    .build();

            transactionRepository.save(transaction);

        } else if (finalPriceInUSD != null) {
            BigDecimal amountOfCurrencyCalculated = finalPriceInUSD.divide(rate, 8, RoundingMode.HALF_UP);

            PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                    .orElseThrow(() -> new AssetNotOwnedException("You do not own this currency"));

            if (portfolioAsset.getAmount().compareTo(amountOfCurrencyCalculated) < 0) {
                throw new InsufficientAssetAmountException("Insufficient amount of currency to sell");
            }

            BigDecimal amountInUSDCalculated = amountOfCurrencyCalculated.multiply(rate).setScale(8, RoundingMode.HALF_UP);

            BigDecimal newAmount = portfolioAsset.getAmount().subtract(amountOfCurrencyCalculated).setScale(8, RoundingMode.HALF_UP);
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
                    .amount(amountOfCurrencyCalculated.setScale(8, RoundingMode.HALF_UP))
                    .rate(rate.setScale(8, RoundingMode.HALF_UP))
                    .timestamp(LocalDateTime.now())
                    .user(user)
                    .portfolio(portfolio)
                    .build();

            transactionRepository.save(transaction);
        }
    }

    public Page<TransactionHistoryDTO> getTransactionHistory(Pageable pageable) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Page<Transaction> transactions = transactionRepository.findByUser(currentUser, pageable);
        return mapTransactionsToDTO(transactions);
    }

    public Page<TransactionHistoryDTO> getTransactionHistoryByPortfolio(Integer portfolioid, Pageable pageable) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Portfolio portfolio = getPortfolioByidAndUser(portfolioid, currentUser);
        Page<Transaction> transactions = transactionRepository.findByUserAndPortfolio(currentUser, portfolio, pageable);
        return mapTransactionsToDTO(transactions);
    }

    private Page<TransactionHistoryDTO> mapTransactionsToDTO(Page<Transaction> transactions) {
        return transactions.map(transaction -> TransactionHistoryDTO.builder()
                .transactionid(transaction.getTransactionid())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .rate(transaction.getRate())
                .timestamp(transaction.getTimestamp())
                .currencyName(transaction.getCurrency().getName())
                .portfolioName(transaction.getPortfolio().getName())
                .build());
    }

    @Transactional
    public Portfolio getPortfolioByidAndUser(Integer portfolioid, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
    }
}
