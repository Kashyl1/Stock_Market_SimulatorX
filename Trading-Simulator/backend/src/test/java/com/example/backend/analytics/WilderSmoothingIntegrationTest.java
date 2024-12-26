package com.example.backend.analytics;

import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

@SpringBootTest
@ActiveProfiles("test")
public class WilderSmoothingIntegrationTest {

    @Autowired
    private WilderSmoothingCalculator wilderSmoothingCalculator;

    @ParameterizedTest(name = "{0}")
    @MethodSource("wilderSmoothingTestCases")
    void testWilderSmoothing(String testName, List<BigDecimal> inputValues, List<BigDecimal> expectedValues, boolean expectException) {
        if (expectException) {
            Assertions.assertThrows(NotEnoughDataForCalculationException.class, () -> {
                wilderSmoothingCalculator.calculate(inputValues);
            }, "Expected NotEnoughDataForCalculationException for: " + testName);
        } else {
            List<BigDecimal> result = wilderSmoothingCalculator.calculate(inputValues);
            Assertions.assertEquals(expectedValues.size(), result.size(), "Size mismatch for test: " + testName);
            for (int i = 0; i < expectedValues.size(); i++) {
                Assertions.assertEquals(0, expectedValues.get(i).compareTo(result.get(i)),
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
                                new BigDecimal("8.0357142857142857143"), // V15 = (7.5 * 13 + 15) / 14
                                new BigDecimal("8.6045918367346938776")  // V16 = (8.0357142857142857143 * 13 + 16) / 14
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
                                new BigDecimal("37.5"), // V14 SMA = 525 / 14 = 37.5
                                new BigDecimal("40.178571428571428571"), // V15 = (37.5 * 13 + 75) / 14
                                new BigDecimal("43.022959183673469387"), // V16 = (40.178571428571428571 * 13 + 80) / 14
                                new BigDecimal("46.021319241982507288")  // V17 = (43.022959183673469387 * 13 + 85) / 14
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
                                BigDecimal.valueOf(-75.0), // SMA = (-10 + -20 + ... + -140) / 14 = -1050 / 14 = -75.0
                                new BigDecimal("-80.357142857142857143"), // V15 = (-75 * 13 + -150) / 14
                                new BigDecimal("-86.045918367346938776")  // V16 = (-80.357142857142857143 * 13 + -160) / 14
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
                                new BigDecimal("8035714.2857142857143"), // V15 = (7.5M * 13 + 15M) / 14
                                new BigDecimal("8604591.8367346938776")  // V16 = (8.0357142857142857143M * 13 + 16M) / 14
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
                        List.of(),
                        null,
                        true
                )
        );
    }

    private static List<BigDecimal> createValues(int start, int end) {
        List<BigDecimal> values = new java.util.ArrayList<>();
        for (int i = start; i <= end; i++) {
            values.add(BigDecimal.valueOf(i));
        }
        return values;
    }

    @Test
    void testWilderSmoothingCalculatorIntegration() {
        List<BigDecimal> inputValues = List.of(
                BigDecimal.valueOf(10), BigDecimal.valueOf(20), BigDecimal.valueOf(30),
                BigDecimal.valueOf(40), BigDecimal.valueOf(50), BigDecimal.valueOf(60),
                BigDecimal.valueOf(70), BigDecimal.valueOf(80), BigDecimal.valueOf(90),
                BigDecimal.valueOf(100), BigDecimal.valueOf(110), BigDecimal.valueOf(120),
                BigDecimal.valueOf(130), BigDecimal.valueOf(140), BigDecimal.valueOf(150)
        );

        List<BigDecimal> expectedValues = List.of(
                BigDecimal.valueOf(75.0), // SMA = (10 + 20 + ... + 140) / 14 = 1050 / 14 = 75.0
                new BigDecimal("80.357142857142857143") // (75 * 13 + 150) / 14 = 975 + 150 / 14 = 1125 / 14 = 80.357142857142857143
        );

        List<BigDecimal> result = wilderSmoothingCalculator.calculate(inputValues);

        Assertions.assertEquals(expectedValues.size(), result.size(), "Size mismatch for test: ");
        for (int i = 0; i < expectedValues.size(); i++) {
            Assertions.assertEquals(0, expectedValues.get(i).compareTo(result.get(i)),
                    "Smoothed value at index " + i + " is incorrect");
        }
    }
}
