package com.example.backend.currency;

import com.example.backend.transaction.CurrencyUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/24hr?symbol=";
    private static final String BINANCE_PRICE_API_URL = "https://api.binance.com/api/v3/ticker/price?symbol=";
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    @Autowired
    private RestTemplate restTemplate;
    private final CurrencyRepository currencyRepository;

    private static final List<String> CURRENCY_SYMBOLS = Arrays.asList(
            "BTC", "ETH", "XMR", "BNB", "SOL", "XRP", "DOGE", "TRX", "TON", "ADA",
            "AVAX", "SHIB", "LINK", "DOT", "DAI", "NEAR", "LTC", "SUI", "APT", "UNI",
            "PEPE", "TAO", "ICP", "FET", "XLM", "STX", "RENDER", "WIF", "IMX",
            "AAVE", "FIL", "ARB", "OP", "INJ", "HBAR", "FTM", "VET",
            "ATOM", "RUNE", "BONK", "GRT", "SEI", "JUP", "FLOKI", "PYTH"
    );


    public void updateCurrentPrice() {
        List<Currency> currenciesToUpdate = new ArrayList<>();
        for (String symbol : CURRENCY_SYMBOLS) {
            String url = BINANCE_PRICE_API_URL + symbol + "USDT";
            try {
                JSONObject response = new JSONObject(restTemplate.getForObject(url, String.class));
                BigDecimal currentPrice = response.getBigDecimal("price");

                Optional<Currency> existingCurrencyOpt = currencyRepository.findBySymbol(symbol);
                if (existingCurrencyOpt.isPresent()) {
                    Currency existingCurrency = existingCurrencyOpt.get();
                    if (currentPrice != null && currentPrice.compareTo(existingCurrency.getCurrentPrice()) != 0) {
                        existingCurrency.setCurrentPrice(currentPrice);
                        currenciesToUpdate.add(existingCurrency);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to update price for currency symbol {}: {}", symbol, e.getMessage());
            }
        }
        if (!currenciesToUpdate.isEmpty()) {
            currencyRepository.saveAll(currenciesToUpdate);
        }
    }


    @Transactional
    public void updateAdditionalData() {
        List<Currency> currenciesToUpdate = new ArrayList<>();
        for (String symbol : CURRENCY_SYMBOLS) {
            String url = BINANCE_API_URL + symbol + "USDT";
            try {
                CurrencyResponse response = restTemplate.getForObject(url, CurrencyResponse.class);

                if (response != null) {
                    Optional<Currency> existingCurrencyOpt = currencyRepository.findBySymbol(symbol);

                    if (existingCurrencyOpt.isPresent()) {
                        Currency existingCurrency = existingCurrencyOpt.get();
                        boolean updated = false;

                        if (response.getPriceChange() != null && !response.getPriceChange().equals(existingCurrency.getPriceChange())) {
                            existingCurrency.setPriceChange(response.getPriceChange());
                            updated = true;
                        }
                        if (response.getPriceChangePercent() != null && !response.getPriceChangePercent().equals(existingCurrency.getPriceChangePercent())) {
                            existingCurrency.setPriceChangePercent(response.getPriceChangePercent());
                            updated = true;
                        }
                        if (response.getHighPrice() != null && !response.getHighPrice().equals(existingCurrency.getHighPrice())) {
                            existingCurrency.setHighPrice(response.getHighPrice());
                            updated = true;
                        }
                        if (response.getLowPrice() != null && !response.getLowPrice().equals(existingCurrency.getLowPrice())) {
                            existingCurrency.setLowPrice(response.getLowPrice());
                            updated = true;
                        }
                        if (response.getVolume() != null && !response.getVolume().equals(existingCurrency.getVolume())) {
                            existingCurrency.setVolume(response.getVolume());
                            updated = true;
                        }

                        if (updated) {
                            currenciesToUpdate.add(existingCurrency);
                        }
                    } else {
                        Currency newCurrency = mapToCurrency(response, symbol);
                        currenciesToUpdate.add(newCurrency);
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to fetch data for currency symbol {}: {}", symbol, e.getMessage());
            }
        }
        if (!currenciesToUpdate.isEmpty()) {
            currencyRepository.saveAll(currenciesToUpdate);
            logger.info("Updated additional data for {} currencies", currenciesToUpdate.size());
        }
    }

    private Currency mapToCurrency(CurrencyResponse response, String symbol) {
        Optional<Currency> existingCurrencyOpt = currencyRepository.findBySymbol(symbol);

        return Currency.builder()
                .symbol(symbol)
                .name(existingCurrencyOpt.map(Currency::getName).orElse(response.getSymbol()))
                .currentPrice(BigDecimal.ZERO)
                .priceChange(response.getPriceChange())
                .priceChangePercent(response.getPriceChangePercent())
                .highPrice(response.getHighPrice())
                .lowPrice(response.getLowPrice())
                .volume(response.getVolume())
                .build();
    }

    public void updateCurrencyNamesAndImages() {
        String coingeckoUrl = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&symbols=";
        String symbols = String.join(",", CURRENCY_SYMBOLS);
        String url = coingeckoUrl + symbols;
        List<Currency> currenciesToUpdate = new ArrayList<>();

        try {
            JSONArray response = new JSONArray(restTemplate.getForObject(url, String.class));
            for (int i = 0; i < response.length(); i++) {
                JSONObject currencyData = response.getJSONObject(i);
                String symbol = currencyData.getString("symbol").toUpperCase();
                String imageUrl = currencyData.getString("image");
                String name = currencyData.getString("name");

                Optional<Currency> existingCurrencyOpt = currencyRepository.findBySymbol(symbol);
                if (existingCurrencyOpt.isPresent()) {
                    Currency existingCurrency = existingCurrencyOpt.get();
                    existingCurrency.setImageUrl(imageUrl);
                    existingCurrency.setName(name);
                    currenciesToUpdate.add(existingCurrency);
                }
            }
            if (!currenciesToUpdate.isEmpty()) {
                currencyRepository.saveAll(currenciesToUpdate);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch data from CoinGecko: {}", e.getMessage());
        }
    }

}
