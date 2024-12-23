package com.example.backend.analytics;

import static com.example.backend.util.CryptoSymbols.CURRENCY_SYMBOLS;

import com.example.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IndicatorScheduler {

    private final AnalyticsService analyticsService;
    private final IndicatorCacheService indicatorCacheService;
    private final List<String> intervals = List.of("1m", "3m", "5m", "30m", "1h", "1d");
    private static final Logger logger = LoggerFactory.getLogger(MacdCalculator.class);

    @LogExecutionTime
    @Scheduled(fixedRate = 1000 * 71)
    public void updateIndicators() {
        for (String symbol : CURRENCY_SYMBOLS) {
            for (String interval : intervals) {
                try {
                    BigDecimal sma = analyticsService.calculateIndicator(symbol, interval, new SmaCalculator(30));
                    indicatorCacheService.saveSma(symbol, interval, 30, sma);
                    logger.debug("Saved SMA for {} {}: {}", symbol, interval, sma);


                    List<BigDecimal> emaSeries = analyticsService.calculateIndicator(symbol, interval, new EmaCalculator(12));
                    BigDecimal latestEma = emaSeries.get(emaSeries.size() - 1);
                    indicatorCacheService.saveEma(symbol, interval, 12, latestEma);

                    BigDecimal rsi = analyticsService.calculateIndicator(symbol, interval, new RsiCalculator(14));
                    indicatorCacheService.saveRsi(symbol, interval, 14, rsi);

                    BigDecimal volatility = analyticsService.calculateIndicator(symbol, interval, new VolatilityCalculator(14));
                    indicatorCacheService.saveVolatility(symbol, interval, 14, volatility);

                    MacdResult macdResult = analyticsService.calculateIndicator(symbol, interval, new MacdCalculator());
                    indicatorCacheService.saveMacd(symbol, interval, macdResult);
                } catch (Exception e) {
                    System.err.println("Failed to update analytics for " + symbol + " " + interval + ": " + e.getMessage());
                }
            }
        }
    }
}
