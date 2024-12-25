package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TrueRangeCalculatorTest {
    // FOrmula: MAX(High Price - Low Price, ABS(High Price - previous close Price), ABS(LowPrice - previous close price)

    @Test
    void testTrueRangeCalculation() {
        List<HistoricalKline> klines = new ArrayList<>();
        klines.add(createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("90")));
        klines.add(createKline(new BigDecimal("105"), new BigDecimal("115"), new BigDecimal("95")));
        klines.add(createKline(new BigDecimal("108"), new BigDecimal("112"), new BigDecimal("102")));

        TrueRangeCalculator trueRangeCalculator = new TrueRangeCalculator();
        List<BigDecimal> trueRange = trueRangeCalculator.calculate(klines);

        List<BigDecimal> expected = List.of(
                new BigDecimal("20.00000"), // Max z 115-95 && abs.115-105 && abs.95-105 is 20
                new BigDecimal("10.00000") // Max z 112-102 && abs.112-105 && abs.102-105 is 10
        );

        Assertions.assertEquals(expected.size(), trueRange.size(), "True range size mismatch");

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(0, trueRange.get(i).compareTo(expected.get(i)),
                    "True range at index: " + i + " did not match the expcted value");
        }
    }

    @Test
    void testTrueRangeWithInsufficientData() {
        List<HistoricalKline> klines = new ArrayList<>();
        klines.add(createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("95")));

        TrueRangeCalculator trueRangeCalculator = new TrueRangeCalculator();
        List<BigDecimal> trueRange = trueRangeCalculator.calculate(klines);

        Assertions.assertTrue(trueRange.isEmpty(), "True range should be empty when klines are <= 1");
    }

    @Test
    void testTrueRangeCalculationWithVaryingValue() {
        List<HistoricalKline> klines = new ArrayList<>();

        klines.add(createKline(new BigDecimal("50"), new BigDecimal("60"), new BigDecimal("40")));
        klines.add(createKline(new BigDecimal("55"), new BigDecimal("65"), new BigDecimal("50")));
        klines.add(createKline(new BigDecimal("60"), new BigDecimal("70"), new BigDecimal("55")));
        klines.add(createKline(new BigDecimal("65"), new BigDecimal("75"), new BigDecimal("60")));

        TrueRangeCalculator trueRangeCalculator = new TrueRangeCalculator();
        List<BigDecimal> trueRange = trueRangeCalculator.calculate(klines);

        List<BigDecimal> expected = List.of(
                new BigDecimal("15.00000"), // TR 2 = max(65-50, abs.65-50, 50-50) = 15
                new BigDecimal("15.00000"), // TR 3 = max(70-55, abs.70-55, abs.55-55 = 15
                new BigDecimal("15.00000")  // TR 3 = max(75-60, abs.75-60, abs.60-60 = 15
        );

        Assertions.assertEquals(expected.size(), trueRange.size(), "True range list mismatch");

        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertEquals(0, trueRange.get(i).compareTo(expected.get(i)));
        }
    }

    private HistoricalKline createKline(BigDecimal closePrice, BigDecimal highPrice, BigDecimal lowPrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        kline.setHighPrice(highPrice);
        kline.setLowPrice(lowPrice);
        return kline;
    }
}
