package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class EmaCalculatorTest {

    /*
     * Formula for EMA (Exponential Moving Average):
     *
     * Step 1: Calculate the Simple Moving Average (SMA) for the first N periods:
     *     SMA = (Price_1 + Price_2 + ... + Price_N) / N
     *
     * Step 2: Calculate the smoothing factor:
     *     K = 2 / (N + 1)
     *
     * Step 3: Calculate EMA iteratively for the remaining periods:
     *     EMA_t = (close price * K) + (previous ema * (1 - K))
     *
     */


    @ParameterizedTest(name = "{0}")
    @MethodSource("emaTestCases")
    void testEmaCalculation(String testName, List<HistoricalKline> klines, int periods, List<BigDecimal> expectedEma, boolean expectException) {
        EmaCalculator emaCalculator = new EmaCalculator(periods);

        if (expectException) {
            Assertions.assertThrows(NotEnoughDataForCalculationException.class, () -> {
                emaCalculator.calculate(klines);
            }, "Expected NotEnoughDataForCalculationException for: " + testName);
        } else {
            List<BigDecimal> resultEma = emaCalculator.calculate(klines);

            Assertions.assertEquals(expectedEma.size(), resultEma.size(),
                    "EMA series size did not match the expected size for: " + testName);

            for (int i = 0; i < expectedEma.size(); i++) {
                Assertions.assertEquals(0, expectedEma.get(i).compareTo(resultEma.get(i)),
                        "EMA value at index " + i + " did not match the expected value for: " + testName);
            }
        }
    }

    static Stream<Arguments> emaTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic calculation with sufficient data",
                        List.of(
                                createKline(new BigDecimal("10")),
                                createKline(new BigDecimal("20")),
                                createKline(new BigDecimal("30")),
                                createKline(new BigDecimal("40")),
                                createKline(new BigDecimal("50"))
                        ),
                        3,
                        List.of(
                                new BigDecimal("20.000"), // SMA
                                new BigDecimal("30.000"), // EMA_4 = (40 * 0.5) + (20 * 0.5)
                                new BigDecimal("40.000")  // EMA_5 = (50 * 0.5) + (30 * 0.5)
                        ),
                        false
                ),
                Arguments.of(
                        "Calculation with insufficient data",
                        List.of(
                                createKline(new BigDecimal("10")),
                                createKline(new BigDecimal("20"))
                        ),
                        3,
                        null,
                        true
                ),
                Arguments.of(
                        "Calculation with constant prices",
                        List.of(
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100")),
                                createKline(new BigDecimal("100"))
                        ),
                        3,
                        List.of(
                                new BigDecimal("100.000"), // SMA
                                new BigDecimal("100.000"), // EMA_4
                                new BigDecimal("100.000")  // EMA_5
                        ),
                        false
                ),
                Arguments.of(
                        "Calculation with decreasing prices",
                        List.of(
                                createKline(new BigDecimal("50")),
                                createKline(new BigDecimal("40")),
                                createKline(new BigDecimal("30")),
                                createKline(new BigDecimal("20")),
                                createKline(new BigDecimal("10"))
                        ),
                        3,
                        List.of(
                                new BigDecimal("40.000"), // SMA
                                new BigDecimal("30.000"), // EMA_4
                                new BigDecimal("20.000")  // EMA_5
                        ),
                        false
                ),
                Arguments.of(
                        "Calculation with negative prices",
                        List.of(
                                createKline(new BigDecimal("-10")),
                                createKline(new BigDecimal("-20")),
                                createKline(new BigDecimal("-30")),
                                createKline(new BigDecimal("-40")),
                                createKline(new BigDecimal("-50"))
                        ),
                        3,
                        List.of(
                                new BigDecimal("-20.000"), // SMA
                                new BigDecimal("-30.000"), // EMA_4
                                new BigDecimal("-40.000")  // EMA_5
                        ),
                        false
                )
        );
    }

    private static HistoricalKline createKline(BigDecimal closePrice) {
        HistoricalKline kline = new HistoricalKline();
        kline.setClosePrice(closePrice);
        return kline;
    }
}