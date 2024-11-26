package com.example.backend.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.json.JSONObject;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Tag(name = "Currency Service", description = "Service for updating and retrieving currency data")
public class CurrencyService {
    // Całkowita zmiana logiki w przyszłości dodawania kryptowalut (LISTA NIE BĘDZIE GIT)
    // PRZYPOMINAJKA CZEMU: BINANCE BIFI, COINGEECKO: beefy-finance
    // PIERWSZY PLAN DODAĆ NOWĄ KOLUMNE COINGEECKO_ID DO ROZWAZENIA
    // DRUGI PLAN MAPOWANIE RĘCZNE
    // TRZECI PLAN BRAK

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;
    private final WebClient binanceClient;
    private final WebClient coingeckoClient;

    private static final List<String> CURRENCY_SYMBOLS = Arrays.asList(
            "BTC", "ETH", "XMR", "BNB", "SOL", "XRP", "DOGE", "TRX", "TON", "ADA",
            "AVAX", "SHIB", "LINK", "DOT", "DAI", "NEAR", "LTC", "SUI", "APT", "UNI",
            "PEPE", "TAO", "ICP", "FET", "XLM", "STX", "RENDER", "WIF", "IMX", "AAVE",
            "FIL", "ARB", "OP", "INJ", "HBAR", "FTM", "VET", "ATOM", "RUNE", "BONK",
            "GRT", "SEI", "JUP", "FLOKI", "PYTH", "TIA", "OM", "ALGO", "ENA", "WLD", // 50
            "MKR", "LDO", "FLOW", "AR", "GALA", "MATIC", "XTZ", "STRK", "EOS",
            "JASMY", "QNT", "BEAM"
    );

    public CurrencyService(
            CurrencyRepository currencyRepository,
            @Qualifier("binanceClient") WebClient binanceClient,
            @Qualifier("coingeckoClient") WebClient coingeckoClient) {
        this.currencyRepository = currencyRepository;
        this.binanceClient = binanceClient;
        this.coingeckoClient = coingeckoClient;
    }

    private List<List<String>> partitionList(List<String> list, int size) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

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
                        currency.setName(symbol);
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
                List<String> symbolsToRequest = CURRENCY_SYMBOLS.stream()
                        .map(symbol -> symbol + "USDT")
                        .collect(Collectors.toList());

                List<List<String>> batches = partitionList(symbolsToRequest, 30);

                Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                        .collect(Collectors.toMap(Currency::getSymbol, currency -> currency));

                List<Mono<String>> monos = new ArrayList<>();

                for (List<String> batch : batches) {
                    String symbolsParam = new ObjectMapper().writeValueAsString(batch);

                    Mono<String> responseMono = binanceClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/v3/ticker/price")
                                    .queryParam("symbols", symbolsParam)
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class);

                    monos.add(responseMono);
                }

                List<Currency> currenciesToUpdate = Flux.merge(monos)
                        .flatMap(responseStr -> {
                            JSONArray responseArray = new JSONArray(responseStr);
                            List<Currency> updatedCurrencies = new ArrayList<>();

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jsonObject = responseArray.getJSONObject(i);
                                String symbolWithUSDT = jsonObject.getString("symbol");
                                String symbol = symbolWithUSDT.replace("USDT", "");
                                BigDecimal currentPrice = jsonObject.getBigDecimal("price");

                                Currency existingCurrency = currencyMap.get(symbol);
                                if (existingCurrency == null) {
                                    logger.error("Currency {} not found in database. Skipping.", symbol);
                                    continue;
                                }

                                synchronized (existingCurrency) {
                                    if (currentPrice != null && (existingCurrency.getCurrentPrice() == null || currentPrice.compareTo(existingCurrency.getCurrentPrice()) != 0)) {
                                        existingCurrency.setCurrentPrice(currentPrice);
                                        updatedCurrencies.add(existingCurrency);
                                    }
                                }
                            }

                            return Flux.fromIterable(updatedCurrencies);
                        })
                        .collectList()
                        .block();

                if (currenciesToUpdate != null && !currenciesToUpdate.isEmpty()) {
                    currencyRepository.saveAll(currenciesToUpdate);
                    logger.info("Updated prices for {} currencies", currenciesToUpdate.size());
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
                List<String> symbolsToRequest = CURRENCY_SYMBOLS.stream()
                        .map(symbol -> symbol + "USDT")
                        .collect(Collectors.toList());

                List<List<String>> batches = partitionList(symbolsToRequest, 50);

                Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                        .collect(Collectors.toMap(Currency::getSymbol, currency -> currency));

                List<Mono<String>> monos = new ArrayList<>();

                for (List<String> batch : batches) {
                    String symbolsParam = new ObjectMapper().writeValueAsString(batch);

                    Mono<String> responseMono = binanceClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/api/v3/ticker/24hr")
                                    .queryParam("symbols", symbolsParam)
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class);

                    monos.add(responseMono);
                }

                List<Currency> currenciesToUpdate = Flux.merge(monos)
                        .flatMap(responseStr -> {
                            JSONArray responseArray = new JSONArray(responseStr);
                            List<Currency> updatedCurrencies = new ArrayList<>();

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject jsonObject = responseArray.getJSONObject(i);
                                String symbolWithUSDT = jsonObject.getString("symbol");
                                String symbol = symbolWithUSDT.replace("USDT", "");

                                Currency existingCurrency = currencyMap.get(symbol);
                                if (existingCurrency == null) {
                                    logger.error("Currency {} not found in database. Skipping.", symbol);
                                    continue;
                                }

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
                                    synchronized (existingCurrency) {
                                        updatedCurrencies.add(existingCurrency);
                                    }
                                }
                            }

                            return Flux.fromIterable(updatedCurrencies);
                        })
                        .collectList()
                        .block();

                assert currenciesToUpdate != null;
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
        try {
            Map<String, Currency> currencyMap = currenciesToUpdate.stream()
                    .collect(Collectors.toMap(c -> c.getSymbol().toUpperCase(), c -> c));

            String responseStr = coingeckoClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/coins/markets")
                            .queryParam("vs_currency", "usd")
                            .queryParam("symbols", symbols)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JSONArray response = new JSONArray(responseStr);

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
            List<String> symbolsList = CURRENCY_SYMBOLS.stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            String symbols = String.join(",", symbolsList);

            try {
                Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                        .collect(Collectors.toMap(Currency::getSymbol, currency -> currency));

                String responseStr = coingeckoClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/v3/coins/markets")
                                .queryParam("vs_currency", "usd")
                                .queryParam("symbols", symbols)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JSONArray response = new JSONArray(responseStr);

                List<Currency> currenciesToUpdate = new ArrayList<>();
                logger.info("API Response: {}", responseStr);

                for (int i = 0; i < response.length(); i++) {
                    JSONObject currencyData = response.getJSONObject(i);
                    String symbol = currencyData.getString("symbol").toUpperCase();
                    String imageUrl = currencyData.optString("image", null);
                    String name = currencyData.optString("name", null);

                    Currency existingCurrency = currencyMap.get(symbol);
                    if (existingCurrency != null) {
                        existingCurrency.setImageUrl(imageUrl);
                        existingCurrency.setName(name);
                        currenciesToUpdate.add(existingCurrency);
                    } else {
                        logger.warn("Currency with symbol {} not found in the database", symbol);
                    }
                }

                logger.info("Currencies to update count: {}", currenciesToUpdate.size());
                if (!currenciesToUpdate.isEmpty()) {
                    currencyRepository.saveAll(currenciesToUpdate);
                    logger.info("Successfully updated {} currencies", currenciesToUpdate.size());
                }

                if (!currenciesToUpdate.isEmpty()) {
                    currencyRepository.saveAll(currenciesToUpdate);
                    logger.info("Updated names and images for {} currencies", currenciesToUpdate.size());
                }
            } catch (Exception e) {
                logger.error("Failed to fetch data from CoinGecko", e);
            }
        });
    }
}
