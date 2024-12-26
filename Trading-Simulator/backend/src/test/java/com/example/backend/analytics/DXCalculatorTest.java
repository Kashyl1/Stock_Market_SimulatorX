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

public class DXCalculatorTest {

    /*
    Formula for DX:
    DX = |PositiveDI - NegativeDI| / (PositiveDI + NegativeDI) * 100
    */

    @ParameterizedTest(name = "{0}")
    @MethodSource("dxTestCases")
    void testDXCalculator(String testName, List<BigDecimal> positiveDI, List<BigDecimal> negativeDI, List<BigDecimal> expected, boolean expectException) {
        DXCalculator dxCalculator = new DXCalculator();

        if (expectException) {
            Assertions.assertThrows(NotEnoughDataForCalculationException.class, () -> {
                dxCalculator.calculate(positiveDI, negativeDI);
            }, "Expected NotEnoughDataForCalculationException for: " + testName);
        } else {
            List<BigDecimal> result = dxCalculator.calculate(positiveDI, negativeDI);
            Assertions.assertEquals(expected.size(), result.size(), "DX series mismatch " + testName);
            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, expected.get(i).compareTo(result.get(i)),
                        "DX at index: " + i + " did not match expected value for: " + testName);
            }
        }
    }

    static Stream<Arguments> dxTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic DX Test",
                        List.of(
                                new BigDecimal("20.00000"),
                                new BigDecimal("25.00000"),
                                new BigDecimal("30.00000")
                        ),
                        List.of(
                                new BigDecimal("15.00000"),
                                new BigDecimal("10.00000"),
                                new BigDecimal("5.00000")
                        ),
                        List.of(
                                new BigDecimal("14.28571"), // |20 - 15| / (20 + 15) * 100 = 5 / 35 * 100 = 14.28571
                                new BigDecimal("42.85714"), // |25 - 10| / (25 + 10) * 100 = 15 / 35 * 100 = 42.85714
                                new BigDecimal("71.42857")  // |30 - 5| / (30 + 5) * 100 = 25 / 35 * 100 = 71.42857
                        ),
                        false
                ),
                Arguments.of(
                        "DX with Zero DI Test",
                        List.of(
                                new BigDecimal("0.00000"),
                                new BigDecimal("25.00000"),
                                new BigDecimal("30.00000")
                        ),
                        List.of(
                                new BigDecimal("0.00000"),
                                new BigDecimal("0.00000"),
                                new BigDecimal("0.00000")
                        ),
                        List.of(
                                new BigDecimal("0.00000"), // |0 - 0| / (0 + 0) * 100 --> / 0 = 0
                                new BigDecimal("100.00000"), // |25 - 0| / (25 + 0) * 100 --> 1
                                new BigDecimal("100.00000")
                        ),
                        false
                ),
                Arguments.of(
                        "DX with different size list // exception",
                        List.of(
                                new BigDecimal("20.00000"),
                                new BigDecimal("25.00000")
                        ),
                        List.of(
                                new BigDecimal("15.00000")
                        ),
                        null,
                        true
                ),
                Arguments.of(
                        "Empty DI list",
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        false
                ),
                Arguments.of(
                        "DX with large values",
                        List.of(
                                new BigDecimal("1000000.00000"),
                                new BigDecimal("2000000.00000"),
                                new BigDecimal("3000000.00000")
                        ),
                        List.of(
                                new BigDecimal("500000.00000"),
                                new BigDecimal("1000000.00000"),
                                new BigDecimal("2000000.00000")
                        ),
                        List.of(
                                new BigDecimal("33.33333"), // |1000000 - 500000| / (1000000 + 500000) * 100 = 500000 / 1500000 * 100 = 33.33333
                                new BigDecimal("33.33333"), // |2000000 - 1000000| / (2000000 + 1000000) * 100 = 33.33333
                                new BigDecimal("20")  // |3000000 - 2000000| / (3000000 + 2000000) * 100 = 20
                        ),
                        false
                )
        );
    }
}
