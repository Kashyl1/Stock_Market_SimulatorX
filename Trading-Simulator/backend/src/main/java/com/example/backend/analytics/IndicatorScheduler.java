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
    private final AdxCalculator adxCalculator;
    private final BullBearPowerCalculator bullBearPowerCalculator;
    private final EmaCalculatorFactory emaCalculatorFactory;

    @LogExecutionTime
    @Scheduled(fixedRate = 1000 * 60 * 30, initialDelay = 1000 * 60 * 5)
    public void updateIndicators() {
        for (String symbol : CURRENCY_SYMBOLS) {
            for (String interval : intervals) {
                try {
                    BigDecimal sma = analyticsService.calculateIndicator(symbol, interval, new SmaCalculator(30));
                    indicatorCacheService.saveSma(symbol, interval, 30, sma);

                    EmaCalculator emaCalc12 = emaCalculatorFactory.create(14);
                    List<BigDecimal> emaSeries12 = analyticsService.calculateIndicator(symbol, interval, emaCalc12);
                    BigDecimal latestEma12 = emaSeries12.get(emaSeries12.size() - 1);
                    indicatorCacheService.saveEma(symbol, interval, 14, latestEma12);

                    BigDecimal rsi = analyticsService.calculateIndicator(symbol, interval, new RsiCalculator(14));
                    indicatorCacheService.saveRsi(symbol, interval, 14, rsi);

                    BigDecimal volatility = analyticsService.calculateIndicator(symbol, interval, new VolatilityCalculator(14));
                    indicatorCacheService.saveVolatility(symbol, interval, 14, volatility);

                    MacdResult macdResult = analyticsService.calculateIndicator(symbol, interval, new MacdCalculator());
                    indicatorCacheService.saveMacd(symbol, interval, macdResult);

                    BigDecimal adx = analyticsService.calculateIndicator(symbol, interval, adxCalculator);
                    indicatorCacheService.saveAdx(symbol, interval, adx);

                    BigDecimal bp = analyticsService.calculateIndicator(symbol, interval, bullBearPowerCalculator);
                    indicatorCacheService.saveBP(symbol, interval, bp);

                    BigDecimal williamsR = analyticsService.calculateIndicator(symbol, interval, new WilliamsRCalculator());
                    indicatorCacheService.saveWilliamsR(symbol, interval, williamsR);
                } catch (Exception e) {
                    System.err.println("Failed to update analytics for " + symbol + " " + interval + ": " + e.getMessage());
                }
            }
        }
    }
}
