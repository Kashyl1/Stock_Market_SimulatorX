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
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final IndicatorCacheService indicatorCacheService;
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    private final AdxCalculator adxCalculator;
    private final BullBearPowerCalculator bullBearPowerCalculator;

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

            List<BigDecimal> emaSeries = analyticsService.calculateIndicator(symbol, interval, new EmaCalculator(periods));
            BigDecimal latestEma = emaSeries.get(emaSeries.size() - 1);

            indicatorCacheService.saveEma(symbol.toUpperCase(), interval, periods, latestEma);
            return ResponseEntity.ok(latestEma);
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

    @GetMapping("/macd/{symbol}/{interval}")
    public ResponseEntity<MacdResult> getMacd(
            @PathVariable String symbol,
            @PathVariable String interval
    ) {
        try {
            MacdResult cached = indicatorCacheService.getMacd(symbol.toUpperCase(), interval);
            if (cached != null) {
                return ResponseEntity.ok(cached.format(8));
            }

            MacdResult result = analyticsService.calculateIndicator(symbol, interval, new MacdCalculator());
            indicatorCacheService.saveMacd(symbol.toUpperCase(), interval, result);
            return ResponseEntity.ok(result.format(8));
        } catch (Exception e) {
            logger.error("Error retrieving MACD for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/adx/{symbol}/{interval}")
    public ResponseEntity<BigDecimal> getAdx(
            @PathVariable String symbol,
            @PathVariable String interval) {
        try {
            BigDecimal cached = indicatorCacheService.getAdx(symbol.toUpperCase(), interval);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }

            BigDecimal adx = analyticsService.calculateIndicator(symbol, interval, adxCalculator);
            indicatorCacheService.saveAdx(symbol.toUpperCase(), interval, adx);

            return ResponseEntity.ok(adx);
        } catch (Exception e) {
            logger.error("Error retrieving ADX for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/bp/{symbol}/{interval}")
    public ResponseEntity<BigDecimal> getBp(
            @PathVariable String symbol,
            @PathVariable String interval) {
        try {
            BigDecimal cached = indicatorCacheService.getBP(symbol.toUpperCase(), interval);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }

            BigDecimal bp = analyticsService.calculateIndicator(symbol, interval, bullBearPowerCalculator);
            indicatorCacheService.saveBP(symbol, interval, bp);

            return ResponseEntity.ok(bp);
        } catch (Exception e) {
            logger.error("Error retrieving BP for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/williamsR/{symbol}/{interval}")
    public ResponseEntity<BigDecimal> getWilliamsR(
            @PathVariable String symbol,
            @PathVariable String interval) {
        try {
            BigDecimal cached = indicatorCacheService.getWilliamsR(symbol.toUpperCase(), interval);
            if (cached != null) {
                return ResponseEntity.ok(cached);
            }

            BigDecimal williamsR = analyticsService.calculateIndicator(symbol, interval, new WilliamsRCalculator());
            indicatorCacheService.saveBP(symbol, interval, williamsR);

            return ResponseEntity.ok(williamsR);
        } catch (Exception e) {
            logger.error("Error retrieving williamsR for {}: {}", symbol, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}