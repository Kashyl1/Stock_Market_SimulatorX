package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class EmaCalculatorTest {

    @Test
    void testEmaCalculation() {
        List<HistoricalKline> klines = new ArrayList<>();
        klines.add(createKline(new BigDecimal("10")));
        klines.add(createKline(new BigDecimal("20")));
        klines.add(createKline(new BigDecimal("30")));
        klines.add(createKline(new BigDecimal("40")));
        klines.add(createKline(new BigDecimal("50")));

        int periods = 3;
        EmaCalculator emaCalculator = new EmaCalculator(periods);

        BigDecimal result = emaCalculator.calculate(klines);

        BigDecimal expected = new BigDecimal("40.00");

        Assertions.assertEquals(0, result.compareTo(expected),
                "EMA calculation did not match the expected value.");
    }

    private HistoricalKline createKline(BigDecimal closePrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        return kline;
    }
}
