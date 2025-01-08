package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class AtrCalculatorTest {

    private final TrueRangeCalculator trueRangeCalculator = Mockito.mock(TrueRangeCalculator.class);
    private final WilderSmoothingCalculator wilderSmoothingCalculator = Mockito.mock(WilderSmoothingCalculator.class);

    private final AtrCalculator atrCalculator = new AtrCalculator(trueRangeCalculator, wilderSmoothingCalculator);

    @Test
    void testCalculate_ThrowsException_WhenNotEnoughData() {
        List<HistoricalKline> klines = new ArrayList<>();
        when(trueRangeCalculator.calculate(klines))
                .thenReturn(List.of(BigDecimal.ONE, BigDecimal.TEN));

        Assertions.assertThrows(NotEnoughDataForCalculationException.class, () -> atrCalculator.calculate(klines));

        verify(wilderSmoothingCalculator, never()).calculate(anyList());
    }

    @Test
    void testCalculate_ReturnsLastValue_WhenSufficientData() {
        List<HistoricalKline> klines = generateKlines();

        List<BigDecimal> mockTrValues = generateBigDecimalSequence(BigDecimal.ONE);
        when(trueRangeCalculator.calculate(klines)).thenReturn(mockTrValues);

        List<BigDecimal> mockAtrValues = generateBigDecimalSequence(BigDecimal.TEN);
        when(wilderSmoothingCalculator.calculate(mockTrValues)).thenReturn(mockAtrValues);

        BigDecimal result = atrCalculator.calculate(klines);

        BigDecimal expectedLast = mockAtrValues.get(mockAtrValues.size() - 1);
        Assertions.assertEquals(expectedLast, result, "ATR should be the last value from Wilder smoothing");

        verify(trueRangeCalculator, times(1)).calculate(klines);
        verify(wilderSmoothingCalculator, times(1)).calculate(mockTrValues);
    }

    private List<HistoricalKline> generateKlines() {
        List<HistoricalKline> klines = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            HistoricalKline kline = new HistoricalKline();
            kline.setHighPrice(BigDecimal.valueOf(100 + i));
            kline.setLowPrice(BigDecimal.valueOf(90 + i));
            kline.setClosePrice(BigDecimal.valueOf(95 + i));
            klines.add(kline);
        }
        return klines;
    }

    private List<BigDecimal> generateBigDecimalSequence(BigDecimal startValue) {
        List<BigDecimal> list = new ArrayList<>();
        BigDecimal val = startValue;
        for (int i = 0; i < 19; i++) {
            list.add(val);
            val = val.add(BigDecimal.ONE);
        }
        return list;
    }
}
