package com.example.backend.CoinGecko;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CoinGeckoService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    private static final Logger logger = LoggerFactory.getLogger(CoinGeckoService.class);

    // Endpointy CoinGecko
    private final String COINGECKO_ASSETS_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=250&page=";
    private final String COINGECKO_EXCHANGE_RATE_URL = "https://api.coingecko.com/api/v3/simple/price?ids={currency}&vs_currencies=usd";

    @Cacheable("availableAssets")
    public List<Map<String, Object>> getAvailableAssets() {
        List<Map<String, Object>> allAssets = new ArrayList<>();

        int totalAssets = 1000;
        int perPage = 250;
        int pages = (int) Math.ceil(totalAssets / (double) perPage);

        for (int page = 1; page <= pages; page++) {
            String url = COINGECKO_ASSETS_URL + page;
            Map<String, Object>[] assetsPage = restTemplate.getForObject(url, Map[].class);
            if (assetsPage != null) {
                allAssets.addAll(List.of(assetsPage));
            }
        }

        return allAssets;
    }

    @Cacheable(value = "exchangeRates", key = "#currency")
    public Map<String, Object> getExchangeRates(String currency) {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + currency + "&vs_currencies=usd";
        logger.info("Fetching exchange rate for currency: {}", currency);
        return restTemplate.getForObject(url, Map.class);
    }

    @Async
    public CompletableFuture<Map<String, Object>> getExchangeRatesAsync(String currency) {
        Cache cache = cacheManager.getCache("exchangeRates");
        if (cache != null) {
            Map<String, Object> cachedRates = cache.get(currency, Map.class);
            if (cachedRates != null) {
                logger.info("Using cached exchange rate for currency: {}", currency);
                return CompletableFuture.completedFuture(cachedRates);
            }
        }

        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + currency + "&vs_currencies=usd";
        logger.info("Fetching exchange rate from API for currency: {}", currency);
        Map<String, Object> rates = restTemplate.getForObject(url, Map.class);

        if (cache != null) {
            cache.put(currency, rates);
        }

        return CompletableFuture.completedFuture(rates);
    }
}
