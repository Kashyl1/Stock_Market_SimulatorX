package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class MacdCalculatorTest {

    @Test
    void testMacdCalculation() {
        // jo je git
    }

    private HistoricalKline createKline(BigDecimal closePrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        return kline;
    }
}