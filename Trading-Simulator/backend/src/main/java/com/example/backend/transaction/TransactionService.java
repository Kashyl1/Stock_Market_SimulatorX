package com.example.backend.transaction;

import com.example.backend.CoinGecko.CoinGeckoService;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CoinGeckoService coinGeckoService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final CurrencyRepository currencyRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public Page<Map<String, Object>> getAvailableAssetsWithPrices(Pageable pageable) {
        try {
            List<Map<String, Object>> assets = coinGeckoService.getAvailableAssets(pageable);

            List<String> currencies = assets.stream()
                    .map(asset -> (String) asset.get("id"))
                    .collect(Collectors.toList());

            Map<String, Map<String, Object>> ratesMap = coinGeckoService.getExchangeRatesBatch(currencies);
            for (Map<String, Object> asset : assets) {
                String baseCurrency = (String) asset.get("id");
                Map<String, Object> rates = ratesMap.get(baseCurrency.toLowerCase());

                if (rates != null && rates.containsKey("usd")) {
                    asset.put("price_in_usd", rates.get("usd"));
                } else {
                    asset.put("price_in_usd", "Unavailable");
                }
            }

            long totalAssets = coinGeckoService.getTotalAssetsCount();
            return new PageImpl<>(assets, pageable, totalAssets);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get available assets with prices.");
        }
    }

    @Transactional
    public void buyAsset(Integer portfolioid, String currencyid, Double amountInUSD) {
        User currentUser = authenticationService.getCurrentUser();
        Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        Currency currency = currencyRepository.findByCoinGeckoid(currencyid.toLowerCase())
                .orElseGet(() -> fetchAndSaveCurrency(currencyid));

        Double rate = getExchangeRate(currencyid);
        Double amountOfCurrency = amountInUSD / rate;

        if (currentUser.getBalance() < amountInUSD) {
            throw new RuntimeException("Insufficient balance");
        }

        currentUser.setBalance(currentUser.getBalance() - amountInUSD);
        userRepository.save(currentUser);
        PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                .orElse(PortfolioAsset.builder()
                        .portfolio(portfolio)
                        .currency(currency)
                        .amount(0.0)
                        .averagePurchasePrice(0.0)
                        .updatedAt(LocalDateTime.now())
                        .build());

        Double totalAmount = portfolioAsset.getAmount() + amountOfCurrency;
        Double totalCost = (portfolioAsset.getAmount() * portfolioAsset.getAveragePurchasePrice()) + amountInUSD;
        Double newAveragePrice = totalCost / totalAmount;

        portfolioAsset.setAmount(totalAmount);
        portfolioAsset.setAveragePurchasePrice(newAveragePrice);
        portfolioAsset.setUpdatedAt(LocalDateTime.now());

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
    public void sellAsset(Integer portfolioid, String currencyid, Double amountOfCurrency) {
        User currentUser = authenticationService.getCurrentUser();
        Portfolio portfolio = portfolioRepository.findByPortfolioidAndUser(portfolioid, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        Currency currency = currencyRepository.findByCoinGeckoid(currencyid.toLowerCase())
                .orElseGet(() -> fetchAndSaveCurrency(currencyid));

        Double rate = getExchangeRate(currencyid);
        Double amountInUSD = amountOfCurrency * rate;

        PortfolioAsset portfolioAsset = portfolioAssetRepository.findByPortfolioAndCurrency(portfolio, currency)
                .orElseThrow(() -> new RuntimeException("You do not own this currency"));

        if (portfolioAsset.getAmount() < amountOfCurrency) {
            throw new RuntimeException("Insufficient amount of currency to sell");
        }

        Double newAmount = portfolioAsset.getAmount() - amountOfCurrency;
        portfolioAsset.setAmount(newAmount);
        portfolioAsset.setUpdatedAt(LocalDateTime.now());

        if (newAmount == 0.0) {
            portfolioAssetRepository.delete(portfolioAsset);
        } else {
            portfolioAssetRepository.save(portfolioAsset);
        }

        currentUser.setBalance(currentUser.getBalance() + amountInUSD);
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

        transactionRepository.save(transaction);
    }

    private Currency fetchAndSaveCurrency(String currencyid) {
        Map<String, Object> currencyData = coinGeckoService.getCurrencyData(currencyid);
        if (currencyData == null) {
            throw new RuntimeException("Failed to fetch currency data from external API");
        }

        String symbol = (String) currencyData.get("symbol");
        String name = (String) currencyData.get("name");
        String country = "Unknown";
        String description = "";

        Object descriptionObj = currencyData.get("description");
        if (descriptionObj instanceof Map) {
            Map<String, Object> descriptionMap = (Map<String, Object>) descriptionObj;
            Object enDescription = descriptionMap.get("en");
            if (enDescription instanceof String) {
                description = (String) enDescription;
            }
        }

        String source = "CoinGecko";

        Currency newCurrency = Currency.builder()
                .symbol(symbol != null ? symbol.toUpperCase() : "UNKNOWN")
                .name(name != null ? name : "UNKNOWN")
                .coinGeckoid(currencyid.toLowerCase())
                .country(country)
                .description(description != null ? description : "")
                .source(source)
                .build();
        return currencyRepository.save(newCurrency);
    }

    private Double getExchangeRate(String currencyid) {
        Map<String, Object> rateMap = coinGeckoService.getExchangeRates(currencyid);
        if (rateMap == null || !rateMap.containsKey(currencyid.toLowerCase())) {
            throw new RuntimeException("Failed to fetch exchange rate for currency");
        }

        Map<String, Object> currencyRates = (Map<String, Object>) rateMap.get(currencyid.toLowerCase());
        if (currencyRates == null || !currencyRates.containsKey("usd")) {
            throw new RuntimeException("Failed to fetch exchange rate for currency");
        }

        return ((Number) currencyRates.get("usd")).doubleValue();
    }
    public Page<TransactionHistoryDTO> getTransactionHistory(Pageable pageable) {
        User currentUser = authenticationService.getCurrentUser();
        logger.info("Fetching paginated transaction history for user: {}", currentUser.getEmail());
        Page<Transaction> transactions = transactionRepository.findByUser(currentUser, pageable);
        return mapTransactionsToDTO(transactions);
    }

    public Page<TransactionHistoryDTO> getTransactionHistoryByPortfolio(Integer portfolioid, Pageable pageable) {
        User currentUser = authenticationService.getCurrentUser();
        logger.info("Fetching paginated transaction history for user: {} and portfolio ID: {}", currentUser.getEmail(), portfolioid);
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
        logger.info("Fetching portfolio with ID: {} for user: {}", portfolioid, user.getEmail());
        return portfolioRepository.findByPortfolioidAndUser(portfolioid, user)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    }

}
