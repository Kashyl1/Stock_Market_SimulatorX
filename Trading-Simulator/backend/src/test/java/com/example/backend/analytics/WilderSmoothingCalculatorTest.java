package com.example.backend.analytics;

import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WilderSmoothingCalculatorTest {

    /* - Wilder Smoothing Calculator
    Implements Wilder's smoothing for values such as TR, +-DM from
    formula:
    For the first value: N = PERIODS = 14
    SMA = Sum of the first N values / N (average)
    For the other values:
    Smoothed Value = ((Previous Smoothed Value * (N - 1)) + Current Value) / N
    */

    @ParameterizedTest(name = "{0}")
    @MethodSource("wilderSmoothingTestCases")
    void testWilderSmoothingCalculator(String testName, List<BigDecimal> values, List<BigDecimal> expected, boolean expectException) {
        WilderSmoothingCalculator calculator = new WilderSmoothingCalculator();

        if (expectException) {
            Assertions.assertThrows(NotEnoughDataForCalculationException.class, () -> {
                calculator.calculate(values);
            }, "Expected NotEnoughDataForCalculationException for: " + testName);
        } else {
            List<BigDecimal> result = calculator.calculate(values);
            Assertions.assertEquals(expected.size(), result.size(), "Size mismatch for test: " + testName);
            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, expected.get(i).compareTo(result.get(i)),
                        "Smoothed value at index " + i + " is incorrect for test: " + testName);
            }
        }
    }

    static Stream<Arguments> wilderSmoothingTestCases() {
        return Stream.of(
                Arguments.of(
                        "Exact Period (14 values)",
                        createValues(1, 14),
                        List.of(
                                BigDecimal.valueOf(7.5) // SMA = (1+2+...+14)/14 = 105/14 = 7.5
                        ),
                        false
                ),
                Arguments.of(
                        "More than Period (16 values)",
                        createValues(1, 16),
                        List.of(
                                new BigDecimal("7.5"), // SMA
                                new BigDecimal("8.0357142857142857143"), // V15 = (7.5 * 13 + 15) / 14 = 8.0357142857142857143
                                new BigDecimal("8.6045918367346938776")  // V16 = (8.53571428571428571428 * 13 + 16) / 14 = 9.48214285714285714284
                        ),
                        false
                ),
                Arguments.of(
                        "Variable Values",
                        List.of(
                                BigDecimal.valueOf(5), BigDecimal.valueOf(10), BigDecimal.valueOf(15),
                                BigDecimal.valueOf(20), BigDecimal.valueOf(25), BigDecimal.valueOf(30),
                                BigDecimal.valueOf(35), BigDecimal.valueOf(40), BigDecimal.valueOf(45),
                                BigDecimal.valueOf(50), BigDecimal.valueOf(55), BigDecimal.valueOf(60),
                                BigDecimal.valueOf(65), BigDecimal.valueOf(70),
                                BigDecimal.valueOf(75), BigDecimal.valueOf(80), BigDecimal.valueOf(85)
                        ),
                        List.of(
                                new BigDecimal("37.5"), // V14 SMA 525 / 14 = 37.5
                                new BigDecimal("40.178571428571428571"), // V15 (37.5 * 13 + 75) / 14 = 40.178571428571428571
                                new BigDecimal("43.022959183673469387"), // (40.178571428571428571 * 13 + 80) / 14 = 43.022959183673469387
                                new BigDecimal("46.021319241982507288") // (43.022959183673469387 * 13 + 85) / 14 =
                        ),
                        false
                ),
                Arguments.of(
                        "Negative Values",
                        List.of(
                                BigDecimal.valueOf(-10), BigDecimal.valueOf(-20), BigDecimal.valueOf(-30),
                                BigDecimal.valueOf(-40), BigDecimal.valueOf(-50), BigDecimal.valueOf(-60),
                                BigDecimal.valueOf(-70), BigDecimal.valueOf(-80), BigDecimal.valueOf(-90),
                                BigDecimal.valueOf(-100), BigDecimal.valueOf(-110), BigDecimal.valueOf(-120),
                                BigDecimal.valueOf(-130), BigDecimal.valueOf(-140),
                                BigDecimal.valueOf(-150), BigDecimal.valueOf(-160)
                        ),
                        List.of(
                                BigDecimal.valueOf(-75.0), // SMA
                                new BigDecimal("-80.357142857142857143"), // (-75 * 13 + -150) / 14 = -80.80.357142857142857143
                                new BigDecimal("-86.045918367346938776") // (-80.35714285714285714285 * 13 + -160) / 14 = -86.045918367346938776
                        ),
                        false
                ),
                Arguments.of(
                        "Large Values",
                        List.of(
                                BigDecimal.valueOf(1_000_000), BigDecimal.valueOf(2_000_000), BigDecimal.valueOf(3_000_000),
                                BigDecimal.valueOf(4_000_000), BigDecimal.valueOf(5_000_000), BigDecimal.valueOf(6_000_000),
                                BigDecimal.valueOf(7_000_000), BigDecimal.valueOf(8_000_000), BigDecimal.valueOf(9_000_000),
                                BigDecimal.valueOf(10_000_000), BigDecimal.valueOf(11_000_000), BigDecimal.valueOf(12_000_000),
                                BigDecimal.valueOf(13_000_000), BigDecimal.valueOf(14_000_000),
                                BigDecimal.valueOf(15_000_000), BigDecimal.valueOf(16_000_000)
                        ),
                        List.of(
                                new BigDecimal("7500000"), // SMA = (1M + 2M + ... + 14M) / 14 = 105M / 14 = 7.5M
                                new BigDecimal("8035714.2857142857143"), // (7.5M * 13 + 15M) / 14 = 8035714.2857142857143
                                new BigDecimal("8604591.8367346938776")  // (8.03571428571428571428M * 13 + 16M) / 14 = 8604591.8367346938776
                        ),
                        false
                ),
                Arguments.of(
                        "Insufficient Data (10 values)",
                        createValues(1, 10),
                        null,
                        true
                ),
                Arguments.of(
                        "Insufficient Data (0 values)",
                        new ArrayList<>(),
                        null,
                        true
                )
        );
    }

    private static List<BigDecimal> createValues(int start, int end) {
        List<BigDecimal> values = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            values.add(BigDecimal.valueOf(i));
        }
        return values;
    }
}
