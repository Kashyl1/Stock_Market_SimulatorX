package com.example.backend.transaction;

import com.example.backend.CoinGecko.CoinGeckoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final CoinGeckoService coinGeckoService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    /**
     * Pobieranie dostępnych aktywów z cenami w USD
     */
    public List<Map<String, Object>> getAvailableAssetsWithPrices() {
        List<Map<String, Object>> assets = coinGeckoService.getAvailableAssets();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map<String, Object> asset : assets) {
            String baseCurrency = (String) asset.get("id");

            CompletableFuture<Void> future = coinGeckoService.getExchangeRatesAsync(baseCurrency)
                    .thenAccept(rates -> {
                        if (rates != null && rates.containsKey("usd")) {
                            asset.put("price_in_usd", rates.get("usd"));
                            logger.info("Fetched price for {}: {}", baseCurrency, rates.get("usd"));
                        } else {
                            asset.put("price_in_usd", "Unavailable");
                            logger.warn("USD price not available for {}", baseCurrency);
                        }
                    }).exceptionally(ex -> {
                        logger.error("Error fetching price for {}: {}", baseCurrency, ex.getMessage());
                        asset.put("price_in_usd", "Error");
                        return null;
                    });

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return assets;
    }
}
