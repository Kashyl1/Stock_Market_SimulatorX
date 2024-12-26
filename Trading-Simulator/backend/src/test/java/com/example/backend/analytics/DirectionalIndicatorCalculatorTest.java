package com.example.backend.analytics;

import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionalIndicatorCalculatorTest {

    private DirectionalIndicatorCalculator directionalIndicatorCalculator;
    /*
    Directional Indicator Calculator
  - Calculate +- DI values from formula:
    - +DI = (+DM Smoothed / TR Smoothed) * 100
    - -DI = (-DM Smoothed / TR Smoothed) * 100
     */
    @BeforeEach
    void setUp() {
        directionalIndicatorCalculator = new DirectionalIndicatorCalculator();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dxTestCases")
    void testCalculateDX(String testName, List<BigDecimal> dmValues, List<BigDecimal> smoothedTR, List<BigDecimal> expectedDX, boolean expectException) {
        if (expectException) {
            assertThrows(NotEnoughDataForCalculationException.class, () -> {
                directionalIndicatorCalculator.calculate(dmValues, smoothedTR);
            }, "Expected NotEnoughDataForCalculationException for: " + testName);
        } else {
            List<BigDecimal> resultDX = directionalIndicatorCalculator.calculate(dmValues, smoothedTR);
            assertNotNull(resultDX, "DX should not be null for: " + testName);
            assertEquals(expectedDX.size(), resultDX.size(), "DX lsit mismatch: " + testName);

            for (int i = 0; i < expectedDX.size(); i++) {
                assertTrue(expectedDX.get(i).subtract(resultDX.get(i)).abs().compareTo(new BigDecimal("0.00001")) < 0,
                        "DX at index: " + i + " did not match the value: " + testName);
            }
        }
    }

    static Stream<Arguments> dxTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic DX test",
                        List.of(
                                new BigDecimal("50.00000"),
                                new BigDecimal("60.00000"),
                                new BigDecimal("70.00000")
                        ),
                        List.of(
                                new BigDecimal("100.00000"),
                                new BigDecimal("120.00000"),
                                new BigDecimal("140.00000")
                        ),
                        List.of(
                                new BigDecimal("50.00000"),  // 50 / 100 * 100 = 50.00000
                                new BigDecimal("50.00000"),  // 60 / 120 * 100 = 50.00000
                                new BigDecimal("50.00000")   // 70 / 140 * 100 = 50.00000
                        ),
                        false
                ),
                Arguments.of(
                        "Calculate DX with zero Smooth",
                        List.of(
                                new BigDecimal("50.00000"),
                                new BigDecimal("60.00000"),
                                new BigDecimal("70.00000")
                        ),
                        List.of(
                                new BigDecimal("0.00000"),
                                new BigDecimal("120.00000"),
                                new BigDecimal("140.00000")
                        ),
                        List.of(
                                new BigDecimal("0.00000"),  // 50 / 0 * 100 = 0.00000
                                new BigDecimal("50.00000"),  // 60 / 120 * 100 = 50.00000
                                new BigDecimal("50.00000")   // 70 / 140 * 100 = 50.00000
                        ),
                        false
                ),
                Arguments.of(
                        "Should throw exception",
                        List.of(
                                new BigDecimal("50.00000"),
                                new BigDecimal("60.00000")
                        ),
                        List.of(
                                new BigDecimal("100.00000"),
                                new BigDecimal("120.00000"),
                                new BigDecimal("140.00000")
                        ),
                        null,
                        true
                ),
                Arguments.of(
                        "Calculate DX with empty lists",
                        List.of(),
                        List.of(),
                        List.of(),
                        false
                ),
                Arguments.of(
                        "Calculate DX with negative DM",
                        List.of(
                                new BigDecimal("-50.00000"),
                                new BigDecimal("-60.00000"),
                                new BigDecimal("-70.00000")
                        ),
                        List.of(
                                new BigDecimal("100.00000"),
                                new BigDecimal("120.00000"),
                                new BigDecimal("140.00000")
                        ),
                        List.of(
                                new BigDecimal("-50.00000"), // -50 / 100 * 100 = -50.00000
                                new BigDecimal("-50.00000"), // -60 / 120 * 100 = -50.00000
                                new BigDecimal("-50.00000")  // -70 / 140 * 100 = -50.00000
                        ),
                        false
                ),
                Arguments.of(
                        "Calculate DX with large values",
                        List.of(
                                new BigDecimal("1000000.00000"),
                                new BigDecimal("2000000.00000"),
                                new BigDecimal("3000000.00000")
                        ),
                        List.of(
                                new BigDecimal("2000000.00000"),
                                new BigDecimal("4000000.00000"),
                                new BigDecimal("6000000.00000")
                        ),
                        List.of(
                                new BigDecimal("50.00000"),  // 1000000 / 2000000 * 100 = 50.00000
                                new BigDecimal("50.00000"),  // 2000000 / 4000000 * 100 = 50.00000
                                new BigDecimal("50.00000")   // 3000000 / 6000000 * 100 = 50.00000
                        ),
                        false
                ),
                Arguments.of(
                        "Calculate DX with negative and 0 values",
                        List.of(
                                new BigDecimal("-50.00000"),
                                new BigDecimal("-60.00000")
                        ),
                        List.of(
                                new BigDecimal("0.00000"),
                                new BigDecimal("120.00000")
                        ),
                        List.of(
                                new BigDecimal("0.00000"),  // -50 / 0 * 100 = 0.00000
                                new BigDecimal("-50.00000")  // -60 / 120 * 100 = -50.00000
                        ),
                        false
                )
        );
    }

    @Test
    void testCalculateDX_WithZeroSmoothedTR() {
        List<BigDecimal> dmValues = List.of(
                new BigDecimal("50.00000"),
                new BigDecimal("60.00000")
        );
        List<BigDecimal> smoothedTR = List.of(
                new BigDecimal("100.00000"),
                new BigDecimal("0.00000")
        );
        List<BigDecimal> expectedDX = List.of(
                new BigDecimal("50.00000"),
                new BigDecimal("0.00000")
        );

        List<BigDecimal> resultDX = directionalIndicatorCalculator.calculate(dmValues, smoothedTR);
        assertNotNull(resultDX, "DX should not be null");
        assertEquals(expectedDX.size(), resultDX.size(), "List mismatch");

        for (int i = 0; i < expectedDX.size(); i++) {
            if (smoothedTR.get(i).compareTo(BigDecimal.ZERO) == 0) {
                assertEquals(0, resultDX.get(i).compareTo(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP)),
                        "DX at index " + i + " should be 0.00000");
            } else {
                assertTrue(expectedDX.get(i).subtract(resultDX.get(i)).abs().compareTo(new BigDecimal("0.00001")) < 0,
                        "DX at index " + i + " did not match expected value");
            }
        }
    }
}
