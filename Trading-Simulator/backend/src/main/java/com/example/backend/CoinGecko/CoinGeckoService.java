package com.example.backend.CoinGecko;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CoinGeckoService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;

    private static final Logger logger = LoggerFactory.getLogger(CoinGeckoService.class);

    private final String COINGECKO_ASSETS_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=250&page=";
    private final String COINGECKO_CURRENCY_DATA_URL = "https://api.coingecko.com/api/v3/coins/{currency}";

    @Cacheable("availableAssets")
    public List<Map<String, Object>> getAvailableAssets(Pageable pageable) {
        List<Map<String, Object>> pagedAssets = new ArrayList<>();

        int page = pageable.getPageNumber() + 1;
        int perPage = pageable.getPageSize();

        String url = COINGECKO_ASSETS_URL + "&per_page=" + perPage + "&page=" + page;
        logger.info("Requesting CoinGecko API: {}", url);
        try {
            Map<String, Object>[] assetsPage = restTemplate.getForObject(url, Map[].class);
            if (assetsPage != null) {
                pagedAssets.addAll(Arrays.asList(assetsPage));
                logger.info("Received {} assets from CoinGecko API", assetsPage.length);
            } else {
                logger.warn("Received null assets from CoinGecko API for url: {}", url);
            }
        } catch (Exception e) {
            logger.error("Error fetching assets from CoinGecko API: ", e);
            throw new RuntimeException("Failed to fetch assets from CoinGecko");
        }

        return pagedAssets;
    }

    @Cacheable(value = "exchangeRatesBatch", key = "#currencies.stream().sorted().collect(T(java.util.stream.Collectors).joining(','))")
    public Map<String, Map<String, Object>> getExchangeRatesBatch(List<String> currencies) {
        String ids = String.join(",", currencies.stream().map(String::toLowerCase).collect(Collectors.toList()));
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + ids + "&vs_currencies=usd";
        logger.info("Requesting CoinGecko exchange rates API: {}", url);
        try {
            Map<String, Map<String, Object>> rates = restTemplate.getForObject(url, Map.class);
            if (rates != null) {
                logger.info("Received exchange rates for {} currencies", rates.size());
            } else {
                logger.warn("Received null exchange rates from CoinGecko API for url: {}", url);
            }
            return rates;
        } catch (Exception e) {
            logger.error("Error fetching exchange rates from CoinGecko API: ", e);
            throw new RuntimeException("Failed to fetch exchange rates from CoinGecko");
        }
    }
    public long getTotalAssetsCount() {
        return 200;
    }

    @Cacheable(value = "exchangeRates", key = "#currency")
    public Map<String, Object> getExchangeRates(String currency) {
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + currency + "&vs_currencies=usd";
        return restTemplate.getForObject(url, Map.class);
    }
    public Map<String, Object> getCurrencyData(String currencyid) {
        String url = COINGECKO_CURRENCY_DATA_URL.replace("{currency}", currencyid.toLowerCase());
        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            logger.error("Error fetching currency data for {}: {}", currencyid, e.getMessage());
            return null;
        }
    }
}
