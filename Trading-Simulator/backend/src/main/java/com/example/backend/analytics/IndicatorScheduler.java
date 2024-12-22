package com.example.backend.analytics;

import static com.example.backend.util.CryptoSymbols.CURRENCY_SYMBOLS;

import com.example.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
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

    @LogExecutionTime
    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void updateIndicators() {
        int periods = 3;
        for (String symbol : CURRENCY_SYMBOLS) {
            for (String interval : intervals) {
                try {
                    BigDecimal sma = analyticsService.calculateIndicator(symbol, interval, new SmaCalculator(periods));
                    indicatorCacheService.saveSma(symbol, interval, periods, sma);
                    BigDecimal ema = analyticsService.calculateIndicator(symbol, interval, new EmaCalculator(periods));
                    indicatorCacheService.saveEma(symbol, interval, periods, ema);
                    BigDecimal rsi = analyticsService.calculateIndicator(symbol, interval, new RsiCalculator(periods));
                    indicatorCacheService.saveRsi(symbol, interval, periods, rsi);
                    BigDecimal volatility = analyticsService.calculateIndicator(symbol, interval, new VolatilityCalculator(periods));
                    indicatorCacheService.saveVolatility(symbol, interval, periods, volatility);
                } catch (Exception e) {
                    System.err.println("Failed to update analytics for " + symbol + " " + interval + ": " + e.getMessage());
                }
            }
        }
    }
}
