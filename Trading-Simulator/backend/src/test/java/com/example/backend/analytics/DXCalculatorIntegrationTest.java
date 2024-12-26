package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.CurrencyNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DXCalculatorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private DXCalculator dxCalculator;

    @Autowired
    private PositiveDMCalculator positiveDMCalculator;

    @Autowired
    private NegativeDMCalculator negativeDMCalculator;

    @ParameterizedTest(name = "{0}")
    @MethodSource("dxTestCases")
    void testDXCalculation(String testName, List<HistoricalKline> klines, List<BigDecimal> expectedDX) {
        Currency currency = createAndSaveCurrency("BTC", "Bitcoin");

        for (HistoricalKline kline : klines) {
            createAndSaveHistoricalKline(currency, "1h", kline.getOpenTime(),
                    kline.getOpenPrice().doubleValue(),
                    kline.getHighPrice().doubleValue(),
                    kline.getLowPrice().doubleValue(),
                    kline.getClosePrice().doubleValue(),
                    kline.getCloseTime());
        }

        try {
            List<BigDecimal> positiveDM = positiveDMCalculator.calculate(klines);
            List<BigDecimal> negativeDM = negativeDMCalculator.calculate(klines);

            List<BigDecimal> dxValues = dxCalculator.calculate(positiveDM, negativeDM);

            assertNotNull(dxValues, "DX values should not be null");
            assertFalse(dxValues.isEmpty(), "DX values should not be empty");
            assertEquals(positiveDM.size(), dxValues.size(),
                    "DX values size should match the size of the Positive DM series");

            for (int i = 0; i < expectedDX.size(); i++) {
                assertEquals(0, expectedDX.get(i).compareTo(dxValues.get(i)),
                        "DX value at index " + i + " does not match the expected value for test: " + testName);
            }
        } catch (CurrencyNotFoundException e) {
            fail("Currency should be found for test: " + testName);
        }
    }

    static Stream<Arguments> dxTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic DX Calculation",
                        List.of(
                                createKline(100, 90, 80, 95, 1000L),
                                createKline(105, 95, 85, 90, 2000L),
                                createKline(110, 100, 90, 95, 3000L)
                        ),
                        List.of(
                                new BigDecimal("100.00000"),
                                new BigDecimal("100.00000")
                        )
                        // 2+DM: (105 - 100) = 5 > (90 - 95) = -5 --> 5 > -5 && 5 > 0 +DM = 5
                        // 2-DM: (90 - 95) = -5 > (105 - 100) // -DM = 0
                        // 3+DM: (110-105) = 5 > (95-100) --> DM = 5
                        // 3-DM: 0
                        // DX2 = 5/5 * 100 = 100
                        // DX3 = 5/5
                ),
                Arguments.of(
                        "DX with Zero DI Values",
                        List.of(
                                createKline(100, 90, 80, 90, 1000L),
                                createKline(100, 90, 80, 90, 2000L),
                                createKline(100, 90, 80, 90, 3000L)
                        ),
                        List.of(
                                new BigDecimal("0.00000"),
                                new BigDecimal("0.00000")
                        )
                ),
                Arguments.of(
                        "DX with Mixed DI Values",
                        List.of(
                                createKline(100, 90, 80, 95, 1000L),
                                createKline(105, 95, 85, 85, 2000L),
                                createKline(110, 100, 90, 100, 3000L),
                                createKline(115, 105, 95, 110, 4000L)
                        ),
                        List.of(
                                new BigDecimal("100.00000"),
                                new BigDecimal("100.00000"),
                                new BigDecimal("100.00000")

                        )
                ),
                Arguments.of(
                        "DX with Negative DI Values",
                        List.of(
                                createKline(100, 90, 80, 85, 1000L),
                                createKline(95, 85, 75, 80, 2000L),
                                createKline(90, 80, 70, 75, 3000L)
                        ),
                        List.of(
                                new BigDecimal("100.00000"),
                                new BigDecimal("100.00000")
                        )
                ),
                Arguments.of(
                        "DX with Large DI Values",
                        List.of(
                                createKline(1000, 900, 800, 950, 1000L),
                                createKline(1050, 950, 850, 900, 2000L),
                                createKline(1100, 1000, 900, 950, 3000L)
                        ),
                        List.of(
                                new BigDecimal("100.00000"),
                                new BigDecimal("100.00000")
                        )
                )
        );
    }

    private static HistoricalKline createKline(double high, double low, double open, double close, long timestamp) {
        return HistoricalKline.builder()
                .highPrice(BigDecimal.valueOf(high))
                .lowPrice(BigDecimal.valueOf(low))
                .openPrice(BigDecimal.valueOf(open))
                .closePrice(BigDecimal.valueOf(close))
                .openTime(timestamp)
                .closeTime(timestamp + 3600)
                .build();
    }
}
