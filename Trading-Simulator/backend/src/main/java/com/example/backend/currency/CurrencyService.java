package com.example.backend.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@Tag(name = "Currency Service", description = "Service for updating and retrieving currency data")
public class CurrencyService {

    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/24hr?symbols=";
    private static final String BINANCE_PRICE_API_URL = "https://api.binance.com/api/v3/ticker/price";
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&symbols=";
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    @Autowired // do zmiany przypominajka
    private RestTemplate restTemplate;

    private final CurrencyRepository currencyRepository;

    private static final List<String> CURRENCY_SYMBOLS = Arrays.asList(
            "BTC", "ETH", "XMR", "BNB", "SOL", "XRP", "DOGE", "TRX", "TON", "ADA",
            "AVAX", "SHIB", "LINK", "DOT", "DAI", "NEAR", "LTC", "SUI", "APT", "UNI",
            "PEPE", "TAO", "ICP", "FET", "XLM", "STX", "RENDER", "WIF", "IMX",
            "AAVE", "FIL", "ARB", "OP", "INJ", "HBAR", "FTM", "VET",
            "ATOM", "RUNE", "BONK", "GRT", "SEI", "JUP", "FLOKI", "PYTH"
    );

    @PostConstruct
    @Operation(summary = "Create new assets", description = "Create assets after the application start")
    public void initializeCurrencies() {
        measureExecutionTime("initializeCurrencies", () -> {
            try {
                Set<String> existingSymbols = currencyRepository.findAll().stream()
                        .map(Currency::getSymbol)
                        .collect(Collectors.toSet());

                List<Currency> newCurrencies = new ArrayList<>();

                for (String symbol : CURRENCY_SYMBOLS) {
                    if (!existingSymbols.contains(symbol)) {
                        Currency currency = new Currency();
                        currency.setSymbol(symbol);
                        currency.setName(symbol + " Royal coin to marka"); // tylko na chwile (dosłownie ~1.2 sekundy) xd
                        newCurrencies.add(currency);
                    }
                }

                if (!newCurrencies.isEmpty()) {
                    currencyRepository.saveAll(newCurrencies);
                    logger.info("Initialized {} new currencies", newCurrencies.size());
                } else {
                    logger.info("All currencies are already initialized");
                }
            } catch (Exception e) {
                logger.error("Failed to initialize currencies", e);
            }
        });
    }

    private void measureExecutionTime(String methodName, Runnable method) {
        long startTime = System.currentTimeMillis();
        try {
            method.run();
        } finally {
            long endTime = System.currentTimeMillis();
            double durationSeconds = (endTime - startTime) / 1000.0;
            logger.info("{} executed in {} seconds", methodName, durationSeconds);
        }
    }

    @Operation(summary = "Update current prices", description = "Updates the current price of all tracked currencies")
    public void updateCurrentPrice() {
        measureExecutionTime("updateCurrentPrice", () -> {
            try {
                String[] symbolsArray = CURRENCY_SYMBOLS.stream()
                        .map(symbol -> symbol + "USDT")
                        .toArray(String[]::new);
                String symbolsParam = new ObjectMapper().writeValueAsString(symbolsArray);

                String url = BINANCE_PRICE_API_URL + "?symbols=" + symbolsParam;

                String responseStr = restTemplate.getForObject(url, String.class);
                JSONArray responseArray = new JSONArray(responseStr);

                List<Currency> currenciesToUpdate = new ArrayList<>();
                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject jsonObject = responseArray.getJSONObject(i);
                    String symbolWithUSDT = jsonObject.getString("symbol");
                    String symbol = symbolWithUSDT.replace("USDT", "");
                    BigDecimal currentPrice = jsonObject.getBigDecimal("price");

                    Optional<Currency> existingCurrencyOpt = currencyRepository.findBySymbol(symbol);
                    if (existingCurrencyOpt.isPresent()) {
                        Currency existingCurrency = existingCurrencyOpt.get();
                        if (currentPrice != null && (existingCurrency.getCurrentPrice() == null || currentPrice.compareTo(existingCurrency.getCurrentPrice()) != 0)) {
                            existingCurrency.setCurrentPrice(currentPrice);
                            currenciesToUpdate.add(existingCurrency);
                        }
                    }
                }
                if (!currenciesToUpdate.isEmpty()) {
                    logger.info("Updated additional prices for {} currencies", currenciesToUpdate.size());
                    currencyRepository.saveAll(currenciesToUpdate);
                }
            } catch (Exception e) {
                logger.error("Failed to update prices", e);
            }
        });
    }

    @Transactional
    @Operation(summary = "Update additional data", description = "Updates additional market data for all tracked currencies")
    public void updateAdditionalData() {
        measureExecutionTime("updateAdditionalData", () -> {
            try {
                String[] symbolsArray = CURRENCY_SYMBOLS.stream()
                        .map(symbol -> symbol + "USDT")
                        .toArray(String[]::new);
                String symbolsParam = new ObjectMapper().writeValueAsString(symbolsArray);

                String url = BINANCE_API_URL + symbolsParam;

                String responseStr = restTemplate.getForObject(url, String.class);
                JSONArray responseArray = new JSONArray(responseStr);

                Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                        .collect(Collectors.toMap(Currency::getSymbol, currency -> currency));

                List<Currency> currenciesToUpdate = new ArrayList<>();

                for (int i = 0; i < responseArray.length(); i++) {
                    JSONObject jsonObject = responseArray.getJSONObject(i);
                    String symbolWithUSDT = jsonObject.getString("symbol");
                    String symbol = symbolWithUSDT.replace("USDT", "");

                    Currency existingCurrency = currencyMap.get(symbol);
                    if (existingCurrency != null) {
                        boolean updated = false;

                        BigDecimal priceChange = new BigDecimal(jsonObject.getString("priceChange"));
                        if (!priceChange.equals(existingCurrency.getPriceChange())) {
                            existingCurrency.setPriceChange(priceChange);
                            updated = true;
                        }

                        BigDecimal priceChangePercent = new BigDecimal(jsonObject.getString("priceChangePercent"));
                        if (!priceChangePercent.equals(existingCurrency.getPriceChangePercent())) {
                            existingCurrency.setPriceChangePercent(priceChangePercent);
                            updated = true;
                        }

                        BigDecimal highPrice = new BigDecimal(jsonObject.getString("highPrice"));
                        if (!highPrice.equals(existingCurrency.getHighPrice())) {
                            existingCurrency.setHighPrice(highPrice);
                            updated = true;
                        }

                        BigDecimal lowPrice = new BigDecimal(jsonObject.getString("lowPrice"));
                        if (!lowPrice.equals(existingCurrency.getLowPrice())) {
                            existingCurrency.setLowPrice(lowPrice);
                            updated = true;
                        }

                        BigDecimal volume = new BigDecimal(jsonObject.getString("volume"));
                        if (!volume.equals(existingCurrency.getVolume())) {
                            existingCurrency.setVolume(volume);
                            updated = true;
                        }

                        if (updated) {
                            currenciesToUpdate.add(existingCurrency);
                        }
                    }
                }

                updateMarketCapData(currenciesToUpdate);

                if (!currenciesToUpdate.isEmpty()) {
                    currencyRepository.saveAll(currenciesToUpdate);
                    logger.info("Updated additional data for {} currencies", currenciesToUpdate.size());
                }
            } catch (Exception e) {
                logger.error("Failed to update additional data", e);
            }
        });
    }

    private void updateMarketCapData(List<Currency> currenciesToUpdate) {
        String symbols = CURRENCY_SYMBOLS.stream()
                .map(String::toLowerCase)
                .collect(Collectors.joining(","));
        String url = COINGECKO_API_URL + symbols;

        try {
            JSONArray response = new JSONArray(restTemplate.getForObject(url, String.class));

            Map<String, Currency> currencyMap = currenciesToUpdate.stream()
                    .collect(Collectors.toMap(c -> c.getSymbol().toUpperCase(), c -> c));

            for (int i = 0; i < response.length(); i++) {
                JSONObject currencyData = response.getJSONObject(i);
                String symbol = currencyData.getString("symbol").toUpperCase();
                BigDecimal marketCap = currencyData.optBigDecimal("market_cap", null);

                Currency currency = currencyMap.get(symbol);
                if (currency != null && marketCap != null && !marketCap.equals(currency.getMarketCap())) {
                    currency.setMarketCap(marketCap);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fetch market cap data from CoinGecko", e);
        }
    }

    @Operation(summary = "Update currency names and images", description = "Fetches and updates currency names and images from CoinGecko")
    public void updateCurrencyNamesAndImages() {
        measureExecutionTime("updateCurrencyNamesAndImages", () -> {
            String symbols = String.join(",", CURRENCY_SYMBOLS);
            String url = COINGECKO_API_URL + symbols;

            try {
                JSONArray response = new JSONArray(restTemplate.getForObject(url, String.class));

                Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                        .collect(Collectors.toMap(Currency::getSymbol, currency -> currency));

                List<Currency> currenciesToUpdate = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    JSONObject currencyData = response.getJSONObject(i);
                    String symbol = currencyData.getString("symbol").toUpperCase();
                    String imageUrl = currencyData.getString("image");
                    String name = currencyData.getString("name");

                    Currency existingCurrency = currencyMap.get(symbol);
                    if (existingCurrency != null) {
                        existingCurrency.setImageUrl(imageUrl);
                        existingCurrency.setName(name);
                        currenciesToUpdate.add(existingCurrency);
                    }
                }
                if (!currenciesToUpdate.isEmpty()) {
                    currencyRepository.saveAll(currenciesToUpdate);
                }
            } catch (Exception e) {
                logger.error("Failed to fetch data from CoinGecko", e);
            }
        });
    }

}
