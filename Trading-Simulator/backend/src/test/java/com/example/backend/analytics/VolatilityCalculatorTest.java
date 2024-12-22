package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

class VolatilityCalculatorTest {

    @Test
    void testVolatilityWithAllSamePrices() {
        List<HistoricalKline> klines = new ArrayList<>();
        klines.add(createKline(new BigDecimal("50")));
        klines.add(createKline(new BigDecimal("50")));
        klines.add(createKline(new BigDecimal("50")));

        int periods = 3;
        VolatilityCalculator calculator = new VolatilityCalculator(periods);

        BigDecimal result = calculator.calculate(klines);
        BigDecimal expected = BigDecimal.ZERO; // bo wszystkie ceny jednakowe

        Assertions.assertEquals(0, result.compareTo(expected),
                "Volatility with all same prices should be zero.");
    }

    @Test
    void testVolatilityCalculation() {
        // Przyjmijmy 3 ceny: 10, 20, 30 -> obliczamy odchylenie standardowe z 3 wartości
        List<HistoricalKline> klines = new ArrayList<>();
        klines.add(createKline(new BigDecimal("10")));
        klines.add(createKline(new BigDecimal("20")));
        klines.add(createKline(new BigDecimal("30")));

        int periods = 3;
        VolatilityCalculator calculator = new VolatilityCalculator(periods);

        BigDecimal result = calculator.calculate(klines);

        BigDecimal expectedApprox = new BigDecimal("8.16");

        Assertions.assertEquals(1, result.setScale(2, RoundingMode.HALF_UP).compareTo(expectedApprox),
                "Volatility calculation match the expected approximate value.");
    }

    private HistoricalKline createKline(BigDecimal closePrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        return kline;
    }
}
