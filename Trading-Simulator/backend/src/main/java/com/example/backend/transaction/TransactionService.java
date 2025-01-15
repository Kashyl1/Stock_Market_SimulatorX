package com.example.backend.transaction;

import com.example.backend.adminEvent.AdminEvent;
import com.example.backend.adminEvent.AdminEventTrackingService;
import com.example.backend.mailVerification.VerificationService;
import com.example.backend.userEvent.UserEventTrackingService;
import com.example.backend.userEvent.UserEvent;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transaction Service", description = "Service for handling transactions such as buying and selling assets")
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final CurrencyRepository currencyRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final TransactionMapper transactionMapper;
    private final VerificationService verificationService;
    private final UserEventTrackingService userEventTrackingService;
    private final AdminEventTrackingService adminEventTrackingService;

    @Operation(summary = "Show assets", description = "Processes the asset show")
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
                        assetMap.put("market_cap", currency.getMarketCap());
                        return assetMap;
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(assets, pageable, currencies.getTotalElements());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get available assets with prices.");
        }
    }


    @Transactional
    @Operation(summary = "Buy an asset", description = "Processes the purchase of an asset")
    public void buyAsset(Integer portfolioid, String currencySymbol, BigDecimal amountInUSD, BigDecimal amountOfCurrency, User user) {

        BigDecimal finalAmountInUSD = null;
        BigDecimal finalAmountOfCurrency = null;
        BigDecimal rate = null;

        BigDecimal purchasedAmountInUSD = null;
        BigDecimal purchasedAmountOfCurrency = null;

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

        rate = currency.getCurrentPrice();
        if (rate == null) {
            throw new PriceNotAvailableException("Current price not available for " + currencySymbol);
        }

        if (finalAmountInUSD != null) {
            BigDecimal amountOfCurrencyCalculated = finalAmountInUSD.divide(rate, 8, RoundingMode.HALF_UP);

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

            purchasedAmountInUSD = finalAmountInUSD;
            purchasedAmountOfCurrency = amountOfCurrencyCalculated;

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
                            .updatedAt(LocalDateTime.now())
                            .build());

            BigDecimal totalAmount = portfolioAsset.getAmount().add(finalAmountOfCurrency);
            BigDecimal totalCost = portfolioAsset.getAmount().multiply(portfolioAsset.getAveragePurchasePrice())
                    .add(amountInUSDCalculated);

            BigDecimal newAveragePrice = totalCost.divide(totalAmount, 8, RoundingMode.HALF_UP);

            portfolioAsset.setAmount(totalAmount);
            portfolioAsset.setAveragePurchasePrice(newAveragePrice);
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

            purchasedAmountInUSD = amountInUSDCalculated;
            purchasedAmountOfCurrency = finalAmountOfCurrency;
        }

        Map<String, Object> details = Map.of(
                "portfolioId", portfolioid,
                "currencySymbol", currencySymbol,
                "amountInUSD", purchasedAmountInUSD,
                "amountOfCurrency", purchasedAmountOfCurrency,
                "rate", rate
        );

        userEventTrackingService.logEvent(user.getEmail(), UserEvent.EventType.BUY_CRYPTO, details);
    }


    @Transactional
    @Operation(summary = "Sell an asset", description = "Processes the sale of an asset")
    public void sellAsset(Integer portfolioid, Integer currencyid, BigDecimal amountOfCurrency, BigDecimal priceInUSD, User user) {

        BigDecimal finalAmountOfCurrency = null;
        BigDecimal finalPriceInUSD = null;
        BigDecimal rate;

        BigDecimal soldAmountInUSD = null;
        BigDecimal soldAmountOfCurrency = null;

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

        rate = currency.getCurrentPrice();
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

            soldAmountInUSD = amountInUSDCalculated;
            soldAmountOfCurrency = finalAmountOfCurrency;

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

            soldAmountInUSD = finalPriceInUSD;
            soldAmountOfCurrency = amountOfCurrencyCalculated;
        }

        Map<String, Object> details = Map.of(
                "portfolioId", portfolioid,
                "currencyId", currencyid,
                "currencySymbol", currency.getSymbol(),
                "amountInUSD", soldAmountInUSD,
                "amountOfCurrency", soldAmountOfCurrency,
                "rate", rate
        );

        userEventTrackingService.logEvent(user.getEmail(), UserEvent.EventType.SELL_CRYPTO, details);
    }


    @Operation(summary = "Show Transaction history for user", description = "Processes the transaction history show by user")
    public Page<TransactionHistoryDTO> getTransactionHistory(Pageable pageable) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Page<Transaction> transactions = transactionRepository.findByUser(currentUser, pageable);
        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Show Transaction history for portfolio", description = "Processes the transaction history show by portfolio")
    public Page<TransactionHistoryDTO> getTransactionHistoryByPortfolio(Integer portfolioid, Pageable pageable) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Portfolio portfolio = getPortfolioByidAndUser(portfolioid, currentUser);
        Page<Transaction> transactions = transactionRepository.findByUserAndPortfolio(currentUser, portfolio, pageable);
        return mapTransactionsToDTO(transactions);
    }

    private Page<TransactionHistoryDTO> mapTransactionsToDTO(Page<Transaction> transactions) {
        return transactions.map(transactionMapper::toDTO);
    }

    @Operation(summary = "Get portfolio by user ID (admin)", description = "Show user portfolio")
    @Transactional
    public Portfolio getPortfolioByidAndUser(Integer portfolioid, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
    }

    @Operation(summary = "Get all transactions (admin)", description = "Show all transactions that have been made (admin use)")
    public Page<TransactionHistoryDTO> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionRepository.findAll(pageable);

        String adminEmail = authenticationService.getCurrentUserEmail();

        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.GET_ALL_TRANSACTIONS);

        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Get all user transactions (admin)", description = "Show all user transactions (admin use)")
    public Page<TransactionHistoryDTO> getTransactionsByUser(Integer userid, Pageable pageable) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Page<Transaction> transactions = transactionRepository.findByUser(user, pageable);

        String adminEmail = authenticationService.getCurrentUserEmail();

        Map<String, Object> details = Map.of(
                "userId", user.getId(),
                "userEmail", user.getEmail(),
                "userName", user.getFirstname(),
                "userLastname", user.getLastname()
        );

        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.GET_TRANSACTIONS_BY_USER, details);

        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Get transaction by user ID (admin)", description = "Show user transactions by ID (admin use)")
    @Transactional(readOnly = true)
    public TransactionHistoryDTO getTransactionById(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId));
        return transactionMapper.toDTO(transaction);
    }

    @Operation(summary = "Get transaction by portfolio ID (admin)", description = "Show user transaction by portfolio ID (admin use)")
    @Transactional(readOnly = true)
    public Page<TransactionHistoryDTO> getTransactionsByPortfolio(Integer portfolioid, Pageable pageable) {
        Portfolio portfolio = portfolioRepository.findById(portfolioid)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
        Page<Transaction> transactions = transactionRepository.findByPortfolio(portfolio, pageable);
        return mapTransactionsToDTO(transactions);
    }

    @Operation(summary = "Mark transaction as suspicious (admin)", description = "Admin can mark user transaction as suspicious (admin use)")
    @Transactional
    public void markTransactionAsSuspicious(Integer transactionid, boolean suspicious) {
        Transaction transaction = transactionRepository.findById(transactionid)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        if (transaction.isSuspicious()) {
            throw new TransactionAlreadyMarkedAsSuspicious("Transaction already marked as suspicious!");
        }

        transaction.setSuspicious(suspicious);
        transactionRepository.save(transaction);

        if (suspicious) {
            getTransactionDataForEmail(transaction);
        }

        String adminEmail = authenticationService.getCurrentUserEmail();

        Map<String, Object> details = Map.of(
                "transactionId", transaction.getTransactionid(),
                "userId", transaction.getUser().getId(),
                "userEmail", transaction.getUser().getEmail()
        );
        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.MARK_TRANSACTION_SUSPICIOUS, details);
    }

    @Operation(summary = "Get all suspicious transactions (admin)", description = "Show suspicious transactions (admin use)")
    @Transactional(readOnly = true)
    public List<TransactionHistoryDTO> getSuspiciousTransactions(BigDecimal thresholdAmount) {
        List<Transaction> transactions = transactionRepository.findByAmountGreaterThan(thresholdAmount);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Mark transaction as high value", description = "Update transaction and mark it as high value")
    @Transactional
    public void markHighValueTransaction(BigDecimal thresholdAmount) {
        List<Transaction> transactions = transactionRepository.findByAmountGreaterThan(thresholdAmount);
        for (Transaction transaction : transactions) {
            User user = transaction.getUser();
            LocalDateTime accountCreatedTime = user.getCreatedAt();
            LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
            boolean isNewUser = accountCreatedTime.isAfter(twoWeeksAgo);

            if (!transaction.isSuspicious() && isNewUser) {
                transaction.setSuspicious(true);
                transactionRepository.save(transaction);
                getTransactionDataForEmail(transaction);
            }
        }
    }

    @Operation(summary = "Mark transactions as frequent", description = "Mark transactions as frequent if user did over 100 transaction in 60 minutes")
    @Transactional
    public void markFrequentTransactions() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Transaction> recentTransactions = transactionRepository.findByTimestampBetween(user, oneHourAgo);
            if (recentTransactions.size() > 100) {
                for (Transaction transaction : recentTransactions) {
                    if (!transaction.isSuspicious()) {
                        transaction.setSuspicious(true);
                        transactionRepository.save(transaction);
                        getTransactionDataForEmail(transaction);
                    }
                }
            }
            user.setBlocked(true);
            userRepository.save(user);
        }
    }

    @Operation(summary = "Get data from transaction", description = "Get data about user and transaction to send email")
    public void getTransactionDataForEmail(Transaction transaction) {
        User user = transaction.getUser();
        Currency currency = transaction.getCurrency();
        BigDecimal amount = transaction.getAmount();
        BigDecimal rate = transaction.getRate();
        String transactionType = transaction.getTransactionType();
        int transactionId = transaction.getTransactionid();
        verificationService.sendSuspiciousTransactionEmail(user, transactionId, currency, amount, rate, transactionType);
    }
}
