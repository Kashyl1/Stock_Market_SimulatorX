package com.example.backend.analytics;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final IndicatorCacheService indicatorCacheService;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);

    @GetMapping("/sma/{symbol}/{interval}/{periods}") // Pomyśleć nad logiką
    public ResponseEntity<BigDecimal> getSimpleMovingAverage(
            @PathVariable String symbol,
            @PathVariable String interval,
            @PathVariable @Min(1) int periods) {
        try {
            BigDecimal cached = indicatorCacheService.getSma(symbol.toUpperCase(), interval, periods);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }

            BigDecimal sma = analyticsService.calculateIndicator(symbol, interval, new SmaCalculator(periods));
            return ResponseEntity.ok(sma);
        } catch (Exception e) {
            logger.error("Error retrieving SMA for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/ema/{symbol}/{interval}/{periods}")
    public ResponseEntity<BigDecimal> getEma(
            @PathVariable String symbol,
            @PathVariable String interval,
            @PathVariable @Min(1) int periods) {
        try {
            BigDecimal cached = indicatorCacheService.getEma(symbol.toUpperCase(), interval, periods);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }

            BigDecimal ema = analyticsService.calculateIndicator(symbol, interval, new EmaCalculator(periods));
            indicatorCacheService.saveEma(symbol.toUpperCase(), interval, periods, ema);
            return ResponseEntity.ok(ema);
        } catch (Exception e) {
            logger.error("Error retrieving EMA for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/rsi/{symbol}/{interval}/{periods}")
    public ResponseEntity<BigDecimal> getRsi(
            @PathVariable String symbol,
            @PathVariable String interval,
            @PathVariable @Min(1) int periods) {
        try {
            BigDecimal cached = indicatorCacheService.getRsi(symbol.toUpperCase(), interval, periods);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }

            BigDecimal rsi = analyticsService.calculateIndicator(symbol, interval, new RsiCalculator(periods));
            indicatorCacheService.saveRsi(symbol.toUpperCase(), interval, periods, rsi);
            return ResponseEntity.ok(rsi);
        } catch (Exception e) {
            logger.error("Error retrieving RSI for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }

    }

    @GetMapping("/volatility/{symbol}/{interval}/{periods}")
    public ResponseEntity<BigDecimal> getVolatility(
            @PathVariable String symbol,
            @PathVariable String interval,
            @PathVariable @Min(1) int periods) {
        try {
            BigDecimal cached = indicatorCacheService.getVolatility(symbol.toUpperCase(), interval, periods);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }

            BigDecimal volatility = analyticsService.calculateIndicator(symbol, interval, new VolatilityCalculator(periods));
            indicatorCacheService.saveVolatility(symbol.toUpperCase(), interval, periods, volatility);
            return ResponseEntity.ok(volatility);
        } catch (Exception e) {
            logger.error("Error retrieving volatility for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}