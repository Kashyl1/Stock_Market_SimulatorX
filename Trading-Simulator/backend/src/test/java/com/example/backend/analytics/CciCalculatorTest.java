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

public class CciCalculatorTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("cciTestCases")
    void testCciCalculator(String testName,
                           List<HistoricalKline> klines,
                           BigDecimal expectedValue,
                           boolean expectException) {
        CciCalculator calculator = new CciCalculator();

        if (expectException) {
            Assertions.assertThrows(
                    NotEnoughDataForCalculationException.class,
                    () -> calculator.calculate(klines),
                    "Expected NotEnoughDataForCalculationEception for: " + testName
            );
        } else {
            BigDecimal result = calculator.calculate(klines);
            Assertions.assertEquals(
                    0,
                    result.compareTo(expectedValue),
                    "Cci result mismatch for test: " + testName
            );
        }
    }

    static Stream<Arguments> cciTestCases() {
        return Stream.of(
        Arguments.of(
                "Not enough data (<20 klines) -> exception",
                createKlines(19, 100, 51, 32),
                null,
                true
        ),
        Arguments.of(
                "All prices the same -> meanDeviation = 0 so Cci = 0",
                createKlinesWithSamePrices(),
                BigDecimal.ZERO,
                false
        ),
        Arguments.of("Standard case: High = 120, low = 100, Close = 110",
                createKlines(21, 120, 100, 110),
                new BigDecimal("126.7"),
                false
        )
        );
    }

    static List<HistoricalKline> createKlinesWithSamePrices() {
        List<HistoricalKline> klines = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            klines.add(
                    HistoricalKline.builder()
                            .highPrice(BigDecimal.valueOf((double) 15))
                            .lowPrice(BigDecimal.valueOf((double) 15))
                            .closePrice(BigDecimal.valueOf((double) 15))
                            .build()
            );
        }
        return klines;
    }

    static List<HistoricalKline> createKlines(int count, double high, double low, double close) {
        List<HistoricalKline> klines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            klines.add(
                    HistoricalKline.builder()
                            .highPrice(BigDecimal.valueOf(high + i))
                            .lowPrice(BigDecimal.valueOf(low + i))
                            .closePrice(BigDecimal.valueOf(close + i))
                            .build()
            );
        }
        return klines;
    }
}
