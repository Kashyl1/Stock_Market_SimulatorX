package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

class RsiCalculatorTest {

    @Test
    void testRsiCalculation() {
        List<HistoricalKline> klines = new ArrayList<>();
        klines.add(createKline(new BigDecimal("10")));
        klines.add(createKline(new BigDecimal("12")));
        klines.add(createKline(new BigDecimal("9")));
        klines.add(createKline(new BigDecimal("13")));

        int periods = 3;
        RsiCalculator rsiCalculator = new RsiCalculator(periods);
        BigDecimal result = rsiCalculator.calculate(klines);

        BigDecimal expected = new BigDecimal("67");
        Assertions.assertEquals(0, result.setScale(2, RoundingMode.HALF_UP).compareTo(expected),
                "RSI calculation do not match the expected value.");
    }

    private HistoricalKline createKline(BigDecimal closePrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        return kline;
    }
}
