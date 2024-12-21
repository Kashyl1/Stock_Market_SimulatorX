package com.example.backend.currency;

import com.example.backend.util.LogExecutionTime;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.backend.util.CryptoSymbols.CURRENCY_SYMBOLS;

@Service
@Tag(name = "Currency Service", description = "Service for updating and retrieving currency data")
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final CurrencyRepository currencyRepository;
    private final WebClient binanceClient;
    private final WebClient coingeckoClient;
    private final HistoricalKlineRepository historicalKlineRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CurrencyService(
            CurrencyRepository currencyRepository,
            @Qualifier("binanceClient") WebClient binanceClient,
            @Qualifier("coingeckoClient") WebClient coingeckoClient,
            HistoricalKlineRepository historicalKlineRepository) {
        this.currencyRepository = currencyRepository;
        this.binanceClient = binanceClient;
        this.coingeckoClient = coingeckoClient;
        this.historicalKlineRepository = historicalKlineRepository;
    }

    private List<List<String>> partitionList(List<String> list, int size) {
        List<List<String>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    @LogExecutionTime
    @PostConstruct
    @Operation(summary = "Create new assets", description = "Create assets after the application start")
    public void initializeCurrencies() {
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
    }

    @LogExecutionTime
    @Transactional
    @Operation(summary = "Update current prices", description = "Updates the current price of all tracked currencies")
    public void updateCurrentPrice() {
        try {
            List<String> symbolsToRequest = CURRENCY_SYMBOLS.stream()
                    .map(symbol -> symbol + "USDT")
                    .collect(Collectors.toList());

            List<Mono<String>> monos = createApiRequests("/api/v3/ticker/price", symbolsToRequest, 30, binanceClient);

            Map<String, Currency> currencyMap = new ConcurrentHashMap<>(currencyRepository.findAll().stream()
                    .collect(Collectors.toMap(Currency::getSymbol, currency -> currency)));

            List<Currency> currenciesToUpdate = Flux.merge(monos)
                    .flatMap(responseStr -> Flux.fromIterable(processApiResponse(responseStr, jsonObject -> {
                        String symbolWithUSDT = jsonObject.getString("symbol");
                        String symbol = symbolWithUSDT.replace("USDT", "");
                        BigDecimal currentPrice = new BigDecimal(jsonObject.getString("price"));

                        Currency existingCurrency = currencyMap.get(symbol);
                        if (existingCurrency == null) {
                            logger.error("Currency {} not found in database. Skipping.", symbol);
                            return null;
                        }

                        if (currentPrice != null && (existingCurrency.getCurrentPrice() == null || currentPrice.compareTo(existingCurrency.getCurrentPrice()) != 0)) {
                            existingCurrency.setCurrentPrice(currentPrice);
                            return existingCurrency;
                        }
                        return null;
                    })))
                    .collectList()
                    .block();

            if (currenciesToUpdate != null && !currenciesToUpdate.isEmpty()) {
                currencyRepository.saveAll(currenciesToUpdate);
            }

        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize symbols for API request", e);
        } catch (Exception e) {
            logger.error("Failed to update prices", e);
        }
    }

    @LogExecutionTime
    @Transactional
    @Operation(summary = "Update additional data", description = "Updates additional market data for all tracked currencies")
    public void updateAdditionalData() {
        try {
            List<String> symbolsToRequest = CURRENCY_SYMBOLS.stream()
                    .map(symbol -> symbol + "USDT")
                    .collect(Collectors.toList());

            List<Mono<String>> monos = createApiRequests("/api/v3/ticker/24hr", symbolsToRequest, 50, binanceClient);

            Map<String, Currency> currencyMap = new ConcurrentHashMap<>(currencyRepository.findAll().stream()
                    .collect(Collectors.toMap(Currency::getSymbol, currency -> currency)));

            List<Currency> currenciesToUpdate = Flux.merge(monos)
                    .flatMap(responseStr -> Flux.fromIterable(processApiResponse(responseStr, jsonObject -> {
                        String symbolWithUSDT = jsonObject.getString("symbol");
                        String symbol = symbolWithUSDT.replace("USDT", "");

                        Currency existingCurrency = currencyMap.get(symbol);
                        if (existingCurrency == null) {
                            logger.error("Currency {} not found in database. Skipping.", symbol);
                            return null;
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
                            return existingCurrency;
                        }
                        return null;
                    })))
                    .collectList()
                    .block();

            if (currenciesToUpdate != null && !currenciesToUpdate.isEmpty()) {
                updateMarketCapData(currenciesToUpdate);
                currencyRepository.saveAll(currenciesToUpdate);
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize symbols for API request", e);
        } catch (Exception e) {
            logger.error("Failed to update additional data", e);
        }
    }

    @Transactional
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
                String marketCapStr = currencyData.optString("market_cap", null);
                BigDecimal marketCap = (marketCapStr != null) ? new BigDecimal(marketCapStr) : null;

                Currency currency = currencyMap.get(symbol);
                if (currency != null && marketCap != null && !marketCap.equals(currency.getMarketCap())) {
                    currency.setMarketCap(marketCap);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fetch market cap data from CoinGecko", e);
        }
    }

    @LogExecutionTime
    @Transactional
    @Operation(summary = "Update currency names and images", description = "Fetches and updates currency names and images from CoinGecko")
    public void updateCurrencyNamesAndImages() {
        List<String> symbolsList = CURRENCY_SYMBOLS.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        String symbols = String.join(",", symbolsList);

        try {
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

            Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                    .collect(Collectors.toMap(Currency::getSymbol, currency -> currency));

            List<Currency> currenciesToUpdate = new ArrayList<>();

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
                }
            }

            if (!currenciesToUpdate.isEmpty()) {
                currencyRepository.saveAll(currenciesToUpdate);
                logger.info("Updated names and images for {} currencies", currenciesToUpdate.size());
            }
        } catch (Exception e) {
            logger.error("Failed to fetch data from CoinGecko", e);
        }
    }

    private List<Mono<String>> createApiRequests(String path, List<String> symbols, int batchSize, WebClient webClient) throws JsonProcessingException, JsonProcessingException {
        List<List<String>> batches = partitionList(symbols, batchSize);
        List<Mono<String>> monos = new ArrayList<>();

        for (List<String> batch : batches) {
            String symbolsParam = objectMapper.writeValueAsString(batch);
            Mono<String> responseMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam("symbols", symbolsParam)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class);
            monos.add(responseMono);
        }

        return monos;
    }

    private List<Currency> processApiResponse(String responseStr, Function<JSONObject, Currency> updateFunction) {
        JSONArray responseArray = new JSONArray(responseStr);
        List<Currency> updatedCurrencies = new ArrayList<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonObject = responseArray.getJSONObject(i);
            Currency updatedCurrency = updateFunction.apply(jsonObject);
            if (updatedCurrency != null) {
                synchronized (updatedCurrency) {
                    updatedCurrencies.add(updatedCurrency);
                }
            }
        }

        return updatedCurrencies;
    }

    @LogExecutionTime
    public void updateHistoricalData(String interval, int limit) {
        try {
            Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                    .collect(Collectors.toMap(Currency::getSymbol, currency -> currency));
            int maxConcurrency = 5;

            Flux.fromIterable(CURRENCY_SYMBOLS)
                    .flatMap(symbol -> {
                        String symbolWithUSDT = symbol + "USDT";
                        Currency currency = currencyMap.get(symbol);
                        if (currency == null) {
                            logger.error("Currency {} not found in database. Skipping.", symbol);
                            return Mono.empty();
                        }

                        return binanceClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/api/v3/klines")
                                        .queryParam("symbol", symbolWithUSDT)
                                        .queryParam("interval", interval)
                                        .queryParam("limit", limit)
                                        .build())
                                .retrieve()
                                .bodyToMono(String.class)
                                .flatMap(responseStr -> processKlinesResponse(responseStr, currency, interval))
                                .onErrorResume(e -> {
                                    logger.error("Error fetching klines for symbol {}", symbolWithUSDT, e);
                                    return Mono.empty();
                                });
                        }, maxConcurrency)
                    .collectList()
                    .block();
        } catch (Exception e) {
            logger.error("Failed to update historical data", e);
        }
    }

    private Mono<Void> processKlinesResponse(String responseStr, Currency currency, String timeInterval) {
        return Mono.fromRunnable(() -> {
            if (responseStr == null) {
                logger.error("No response for symbol {}", currency.getSymbol());
                return;
            }

            JSONArray klines = new JSONArray(responseStr);
            List<HistoricalKline> historicalKlines = new ArrayList<>();

            for (int i = 0; i < klines.length(); i++) {
                JSONArray kline = klines.getJSONArray(i);
                Long openTime = kline.getLong(0);
                BigDecimal openPrice = new BigDecimal(kline.getString(1));
                BigDecimal highPrice = new BigDecimal(kline.getString(2));
                BigDecimal lowPrice = new BigDecimal(kline.getString(3));
                BigDecimal closePrice = new BigDecimal(kline.getString(4));
                BigDecimal volume = new BigDecimal(kline.getString(5));
                Long closeTime = kline.getLong(6);

                Optional<HistoricalKline> existingKlineOpt = historicalKlineRepository.findByCurrencyAndTimeIntervalAndOpenTime(
                        currency, timeInterval, openTime);
                HistoricalKline historicalKline;
                if (existingKlineOpt.isPresent()) {
                    historicalKline = existingKlineOpt.get();
                    historicalKline.setOpenPrice(openPrice);
                    historicalKline.setHighPrice(highPrice);
                    historicalKline.setLowPrice(lowPrice);
                    historicalKline.setClosePrice(closePrice);
                    historicalKline.setVolume(volume);
                    historicalKline.setCloseTime(closeTime);
                } else {
                    historicalKline = HistoricalKline.builder()
                            .currency(currency)
                            .openTime(openTime)
                            .openPrice(openPrice)
                            .highPrice(highPrice)
                            .lowPrice(lowPrice)
                            .closePrice(closePrice)
                            .volume(volume)
                            .closeTime(closeTime)
                            .timeInterval(timeInterval)
                            .build();
                }

                historicalKlines.add(historicalKline);
            }

            if (!historicalKlines.isEmpty()) {
                historicalKlineRepository.saveAll(historicalKlines);
            } else {
                logger.info("No new klines found for symbol {}", currency.getSymbol());
            }
        });
    }
}
