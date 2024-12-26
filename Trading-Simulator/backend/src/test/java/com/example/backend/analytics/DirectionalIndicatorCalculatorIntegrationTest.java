package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.currency.HistoricalKlineRepository;
import com.example.backend.exceptions.CurrencyNotFoundException;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DirectionalIndicatorCalculatorIntegrationTest extends BaseIntegrationTest {

    /*
    Directional Indicator Calculator
    - Calculate +- DI values from formula:
    - +DI = (+DM Smoothed / TR Smoothed) * 100
    - -DI = (-DM Smoothed / TR Smoothed) * 100
     */

    @Autowired
    private DirectionalIndicatorCalculator directionalIndicatorCalculator;

    @Autowired
    private PositiveDMCalculator positiveDMCalculator;

    @Autowired
    private NegativeDMCalculator negativeDMCalculator;

    @Autowired
    private HistoricalKlineRepository historicalKlineRepository;

    @ParameterizedTest(name = "{0}")
    @MethodSource("dxIntegrationTestCases")
    void testCalculateDirectionalIndicatorsIntegration(
            String testName,
            List<HistoricalKline> klines,
            List<BigDecimal> expectedPosDI,
            List<BigDecimal> expectedNegDI
    ) {
        Currency currency = createAndSaveCurrency("ETH", "Ethereum");

        for (HistoricalKline kline : klines) {
            createAndSaveHistoricalKline(currency, "1h", kline.getOpenTime(),
                    kline.getOpenPrice().doubleValue(),
                    kline.getHighPrice().doubleValue(),
                    kline.getLowPrice().doubleValue(),
                    kline.getClosePrice().doubleValue(),
                    kline.getCloseTime());
        }

        try {
            List<HistoricalKline> fetchedKlines = historicalKlineRepository.findByCurrencyAndTimeIntervalOrderByOpenTimeAsc(currency, "1h");

            List<BigDecimal> positiveDM = positiveDMCalculator.calculate(fetchedKlines);
            List<BigDecimal> negativeDM = negativeDMCalculator.calculate(fetchedKlines);

            List<BigDecimal> smoothedTR = calculateSmoothedTR(positiveDM, negativeDM);

            List<BigDecimal> positiveDI = directionalIndicatorCalculator.calculate(positiveDM, smoothedTR);

            List<BigDecimal> negativeDI = directionalIndicatorCalculator.calculate(negativeDM, smoothedTR);

            assertNotNull(positiveDI, "Positive DI should not be null for " + testName);
            assertFalse(positiveDI.isEmpty(), "Positive DI should not be empty: " + testName);
            assertEquals(expectedPosDI.size(), positiveDI.size(),
                    "Positive DI List mismatch: " + testName);

            for (int i = 0; i < expectedPosDI.size(); i++) {
                assertTrue(expectedPosDI.get(i).subtract(positiveDI.get(i)).abs().compareTo(new BigDecimal("0.00001")) < 0,
                        "Positive DI at index " + i + " did not match: " + testName);
            }

            assertNotNull(negativeDI, "Negative DI should not be null for " + testName);
            assertFalse(negativeDI.isEmpty(), "Negative DI should not be empty: " + testName);
            assertEquals(expectedNegDI.size(), negativeDI.size(),
                    "Negative DI List mismatch: " + testName);

            for (int i = 0; i < expectedNegDI.size(); i++) {
                assertTrue(expectedNegDI.get(i).subtract(negativeDI.get(i)).abs().compareTo(new BigDecimal("0.00001")) < 0,
                        "Negative DI at inedx " + i + " did not match expected value : " + testName);
            }

        } catch (CurrencyNotFoundException e) {
            fail("Currency should be found: " + testName);
        } catch (NotEnoughDataForCalculationException e) {
            fail("Not enough data for DI calculation: " + testName);
        }
    }

    static Stream<Arguments> dxIntegrationTestCases() {
        return Stream.of(
                Arguments.of(
                        "Integration Test - basic DI calculatiions",
                        List.of(
                                createKline(100, 90, 80, 95, 1000L),
                                createKline(105, 95, 85, 90, 2000L),
                                createKline(110, 100, 90, 95, 3000L)
                        ),
                        List.of(
                                new BigDecimal("100.00000"),  // +DI: 5 / 5 * 100 = 100.00000
                                new BigDecimal("100.00000")  // +DI: 5 / 5 * 100 = 100.00000
                        ),
                        List.of(
                                new BigDecimal("0.00000"),  // -DI: 0 / 5 * 100 = 0.00000
                                new BigDecimal("0.00000")  // -DI: 0 / 5 * 100 = 0.00000
                        )
                ),
                Arguments.of(
                        "Integration Test - DI with zero smoothedTR value",
                        List.of(
                                createKline(100, 90, 80, 90, 1000L),
                                createKline(100, 90, 80, 90, 2000L),
                                createKline(100, 90, 80, 90, 3000L)
                        ),
                        List.of(
                                new BigDecimal("0.00000"),  // +DI: 0 / 0 * 100 = 0.00000
                                new BigDecimal("0.00000")  // +DI: 0 / 0 * 100 = 0.00000
                        ),
                        List.of(
                                new BigDecimal("0.00000"),
                                new BigDecimal("0.00000")
                        )
                ),
                Arguments.of(
                        "Integration Test - DI with mixed DM",
                        List.of(
                                createKline(100, 90, 80, 95, 1000L),
                                createKline(105, 95, 85, 85, 2000L),
                                createKline(110, 100, 90, 100, 3000L),
                                createKline(115, 105, 95, 110, 4000L)
                        ),
                        List.of(
                                new BigDecimal("100.00000"),  // +DI:(5 /5) * 100 = 100.00000
                                new BigDecimal("100.00000"),  // +DI: (6 / 6) * 100 = 100.00000
                                new BigDecimal("100.00000")  // +DI: (7 / 7) * 100 = 100.00000
                        ),
                        List.of(
                                new BigDecimal("0.00000"),  // -DI: X / 0
                                new BigDecimal("0.00000"),  // -DI: 0
                                new BigDecimal("0.00000")  // -DI: 0
                        )
                )
        );
    }


    private List<BigDecimal> calculateSmoothedTR(List<BigDecimal> positiveDM, List<BigDecimal> negativeDM) {
        List<BigDecimal> smoothedTR = new java.util.ArrayList<>();
        for (int i = 0; i < positiveDM.size(); i++) {
            BigDecimal pos = positiveDM.get(i);
            BigDecimal neg = negativeDM.get(i);
            smoothedTR.add(pos.add(neg).setScale(5, RoundingMode.HALF_UP));
        }
        return smoothedTR;
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
