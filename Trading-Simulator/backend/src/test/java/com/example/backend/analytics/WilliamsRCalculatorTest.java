package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WilliamsRCalculatorTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("williamsRTestCases")
    void testWilliamsRCalculator(String testName,
                                 List<HistoricalKline> klines,
                                 BigDecimal expectedValue,
                                 boolean expectException) {
        WilliamsRCalculator calculator = new WilliamsRCalculator();

        if (expectException) {
            Assertions.assertThrows(NotEnoughDataForCalculationException.class,
                    () -> calculator.calculate(klines),
                    "Expected NotEnoughDataForCalculationException for: " + testName);
        } else {
            BigDecimal result = calculator.calculate(klines);
            Assertions.assertEquals(0, result.compareTo(expectedValue),
                    "Williams %R result mismatch in: " + testName);
        }
    }

    static Stream<Arguments> williamsRTestCases() {
        return Stream.of(
                Arguments.of(
                        "Not enough data (<14 klines), expect exception",
                        createKlines(13, 100, 80, 90),
                        null,
                        true
                ),
                Arguments.of(
                        "Standard case: Highest=100, Lowest=80, Close=90",
                        createKlines(14, 100, 80, 90),
                        new BigDecimal("-50.00000000"),
                        false
                ),
                Arguments.of(
                        "Highest == lowest so denominator = 0 and result = 0",
                        createKlinesSameHighLow(100),
                        BigDecimal.ZERO,
                        false
                ),
                Arguments.of(
                        "Close = Highest so (Highest - Highest) / (Highest - lowest) * -100 = 0",
                        createKlines(14, 100, 80, 100),
                        BigDecimal.ZERO,
                        false
                ),
                Arguments.of(
                        "Close = Lowest so (Highest - Lowest) / (Highest - Lowest) * -100 = -100",
                        createKlines(14, 100, 80, 80),
                        new BigDecimal("-100.00000000"),
                        false
                )
        );
    }

    static List<HistoricalKline> createKlines(int count, double high, double low, double close) {
        List<HistoricalKline> klines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            klines.add(
                    HistoricalKline.builder()
                            .highPrice(BigDecimal.valueOf(high))
                            .lowPrice(BigDecimal.valueOf(low))
                            .closePrice(BigDecimal.valueOf(close))
                            .build()
            );
        }
        return klines;
    }

    static List<HistoricalKline> createKlinesSameHighLow(double close) {
        List<HistoricalKline> klines = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            klines.add(
                    HistoricalKline.builder()
                            .highPrice(BigDecimal.valueOf((double) 100))
                            .lowPrice(BigDecimal.valueOf((double) 100))
                            .closePrice(BigDecimal.valueOf(close))
                            .build()
            );
        }
        return klines;
    }
}
