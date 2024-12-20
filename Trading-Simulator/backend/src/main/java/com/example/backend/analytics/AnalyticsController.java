package com.example.backend.analytics;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/sma/{symbol}/{interval}/{periods}")
    public ResponseEntity<BigDecimal> getSimpleMovingAverage(
            @PathVariable String symbol,
            @PathVariable String interval,
            @PathVariable int periods) {
        BigDecimal sma = analyticsService.calculateIndicator(symbol, interval, new SmaCalculator(periods));
        return ResponseEntity.ok(sma);
    }
}
