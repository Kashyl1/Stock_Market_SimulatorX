package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class AtrCalculatorIntegrationTest {

    @Autowired
    private TrueRangeCalculator trueRangeCalculator;

    @Autowired
    private WilderSmoothingCalculator wilderSmoothingCalculator;

    @Autowired
    private AtrCalculator atrCalculator;

    @Test
    void testAtrCalculatorIntegration_ThrowsException_WhenNotEnoughData() {
        List<HistoricalKline> klines = createKlines(10);

        Assertions.assertThrows(NotEnoughDataForCalculationException.class, () -> {
            atrCalculator.calculate(klines);
        });
    }

    @Test
    void testAtrCalculatorIntegration_ReturnsCorrectLastValue() {
        List<HistoricalKline> klines = createKlines(20);

        BigDecimal atrValue = atrCalculator.calculate(klines);


        Assertions.assertNotNull(atrValue, "ATR value should not be null");
        Assertions.assertEquals(0, atrValue.compareTo(new BigDecimal("18.84000")), "ATR should match expected value");
    }

    private List<HistoricalKline> createKlines(int count) {
        List<HistoricalKline> klines = new ArrayList<>();
        BigDecimal base = BigDecimal.valueOf(23);
        for (int i = 0; i < count; i++) {
            HistoricalKline k = new HistoricalKline();
            k.setHighPrice(base.add(BigDecimal.valueOf(i + 31.15)));
            k.setLowPrice(base.add(BigDecimal.valueOf(i + 12.31)));
            k.setClosePrice(base.add(BigDecimal.valueOf(i + 22.46)));
            klines.add(k);
        }
        return klines;
    }
}
