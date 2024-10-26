package com.example.backend.transaction;

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
                        assetMap.put("volume_24h", currency.getVolume());
                        assetMap.put("image_url", currency.getImageUrl());
                        assetMap.put("currencyid", currency.getCurrencyid());
                        return assetMap;
                    })
                    .collect(Collectors.toList());

            return new PageImpl<>(assets, pageable, currencies.getTotalElements());
        } catch (Exception e) {
            logger.error("Failed to get available assets with prices.", e);
            throw new RuntimeException("Failed to get available assets with prices.");
        }
    }


    @Transactional
    public void buyAsset(Integer portfolioid, String currencySymbol, BigDecimal amountInUSD) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        Currency currency = currencyRepository.findBySymbol(currencySymbol.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Currency not found in database"));

        BigDecimal rate = currency.getCurrentPrice();
        if (rate == null) {
            throw new RuntimeException("Current price not available for " + currencySymbol);
        }

        BigDecimal amountOfCurrency = amountInUSD.divide(rate, 8, BigDecimal.ROUND_HALF_UP);

        if (currentUser.getBalance().compareTo(amountInUSD) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        currentUser.setBalance(currentUser.getBalance().subtract(amountInUSD));
        userRepository.save(currentUser);

        PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                .orElse(PortfolioAsset.builder()
                        .portfolio(portfolio)
                        .currency(currency)
                        .amount(BigDecimal.ZERO)
                        .averagePurchasePrice(BigDecimal.ZERO)
                        .currentPrice(rate)
                        .updatedAt(LocalDateTime.now())
                        .build());

        BigDecimal totalAmount = portfolioAsset.getAmount().add(amountOfCurrency);
        BigDecimal totalCost = portfolioAsset.getAmount().multiply(portfolioAsset.getAveragePurchasePrice())
                .add(amountInUSD);

        BigDecimal newAveragePrice = totalAmount.compareTo(BigDecimal.ZERO) == 0
                ? amountOfCurrency
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
                .amount(amountOfCurrency)
                .rate(rate)
                .timestamp(LocalDateTime.now())
                .user(currentUser)
                .portfolio(portfolio)
                .build();

        transactionRepository.save(transaction);
    }

    @Transactional
    public void sellAsset(Integer portfolioid, Integer currencyid, BigDecimal amountOfCurrency) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);

        Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        Currency currency = currencyRepository.findById(currencyid)
                .orElseThrow(() -> new RuntimeException("Currency not found in database"));

        BigDecimal rate = currency.getCurrentPrice();
        if (rate == null) {
            throw new RuntimeException("Current price not available for " + currencyid);
        }

        BigDecimal amountInUSD = amountOfCurrency.multiply(rate);

        PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                .orElseThrow(() -> new RuntimeException("You do not own this currency"));

        if (portfolioAsset.getAmount().compareTo(amountOfCurrency) < 0) {
            throw new RuntimeException("Insufficient amount of currency to sell");
        }

        BigDecimal newAmount = portfolioAsset.getAmount().subtract(amountOfCurrency);
        portfolioAsset.setAmount(newAmount);
        portfolioAsset.setUpdatedAt(LocalDateTime.now());
        portfolio.setUpdatedAt(LocalDateTime.now());
        portfolioRepository.save(portfolio);

        if (newAmount.compareTo(BigDecimal.ZERO) == 0) {
            portfolioAssetRepository.delete(portfolioAsset);
        } else {
            portfolioAssetRepository.save(portfolioAsset);
        }

        currentUser.setBalance(currentUser.getBalance().add(amountInUSD));
        userRepository.save(currentUser);

        Transaction transaction = Transaction.builder()
                .currency(currency)
                .transactionType("SELL")
                .amount(amountOfCurrency)
                .rate(rate)
                .timestamp(LocalDateTime.now())
                .user(currentUser)
                .portfolio(portfolio)
                .build();
        logger.info("Selling asset for currency ID: {}", currencyid);

        transactionRepository.save(transaction);
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
        return portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    }

}
