package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class SmaCalculatorTest {

    // Tescik ma 5 świec ich cena zamkniecia ponize 10-20-30-40-50
    // Dajemy okres na 3 przez co sprawdzamy ostatnie 3 ceny zamknięcia dla naszych świec wynik 40 git jo je git
    @Test
    void testSmaCalculation() {

        List<HistoricalKline> klines = new ArrayList<>();
        klines.add(createKline(new BigDecimal("10")));
        klines.add(createKline(new BigDecimal("20")));
        klines.add(createKline(new BigDecimal("30")));
        klines.add(createKline(new BigDecimal("40")));
        klines.add(createKline(new BigDecimal("50")));

        int periods = 3;
        SmaCalculator smaCalculator = new SmaCalculator(periods);

        BigDecimal result = smaCalculator.calculate(klines);
        BigDecimal expected = new BigDecimal("40");

        Assertions.assertEquals(expected, result, "SMA calculation did not match the expected value.");
    }

    private HistoricalKline createKline(BigDecimal closePrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        return kline;
    }
}
