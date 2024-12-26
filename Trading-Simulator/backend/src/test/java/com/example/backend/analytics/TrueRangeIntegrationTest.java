package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.CurrencyNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
public class TrueRangeIntegrationTest extends BaseIntegrationTest {

    // FOrmula: MAX(High Price - Low Price, ABS(High Price - previous close Price), ABS(LowPrice - previous close price)

    @ParameterizedTest(name = "{0}")
    @MethodSource("trueRangeIntegrationTestCases")
    void testCalculateTrueRangeFromDatabaseUsingCalculateIndicator(String testName, String symbol, String interval,
                                                                   List<HistoricalKline> klines, List<BigDecimal> expected) {
        Currency currency = createAndSaveCurrency(symbol, "toMarka");

        for (HistoricalKline kline : klines) {
            createAndSaveHistoricalKline(currency, interval, kline.getOpenTime(),
                    kline.getOpenPrice().doubleValue(),
                    kline.getHighPrice().doubleValue(),
                    kline.getLowPrice().doubleValue(),
                    kline.getClosePrice().doubleValue(),
                    kline.getCloseTime());
        }

        try {
            TrueRangeCalculator trueRangeCalculator = new TrueRangeCalculator();
            List<BigDecimal> trueRangeSeries = analyticsService.calculateIndicator(symbol, interval, trueRangeCalculator);
            Assertions.assertEquals(expected.size(), trueRangeSeries.size(), "True Range series size mismatch for " + testName);

            for (int i = 0; i < expected.size(); i++) {
                Assertions.assertEquals(0, expected.get(i).compareTo(trueRangeSeries.get(i)),
                        "True Range at index: " + i + " did not match the expected value for " + testName);
            }
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found for " + testName);
        }
    }

    static Stream<Arguments> trueRangeIntegrationTestCases() {
        return Stream.of(
                Arguments.of(
                        "Basic True Range Integration Test",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("90")),
                                createKline(new BigDecimal("105"), new BigDecimal("115"), new BigDecimal("95")),
                                createKline(new BigDecimal("108"), new BigDecimal("112"), new BigDecimal("102"))
                        ),
                        List.of(
                                new BigDecimal("20.00000"), // TR K2: max(115-95=20, abs.115-100=15, abs.95-100=5) = 20
                                new BigDecimal("10.00000")  // TR K3: max(112-102=10, abs.112-105=7, abs.102-105=3) = 10
                        )
                ),
                Arguments.of(
                        "True Range With Insufficient Data",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("95"))
                        ),
                        List.of() // Kline size cant be <= 1
                ),
                Arguments.of(
                        "True Range Calculation With Varying Values",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("50"), new BigDecimal("60"), new BigDecimal("40")),
                                createKline(new BigDecimal("55"), new BigDecimal("65"), new BigDecimal("50")),
                                createKline(new BigDecimal("60"), new BigDecimal("70"), new BigDecimal("55")),
                                createKline(new BigDecimal("65"), new BigDecimal("75"), new BigDecimal("60"))
                        ),
                        List.of(
                                new BigDecimal("15.00000"), // TR K2: max(65-50=15, abs.50-50=0, abs.65-50=15) = 15
                                new BigDecimal("15.00000"), // TR K3: max(70-55=15, abs.70-55=15, abs.55-55=0) = 15
                                new BigDecimal("15.00000")  // TR K4: max(75-60=15, abs.75-60=15, abs.60-60=0) = 15
                        )
                ),
                Arguments.of(
                        "True Range Calculation With Varying High and Low Prices",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("105"), new BigDecimal("95")),
                                createKline(new BigDecimal("102"), new BigDecimal("108"), new BigDecimal("90")),
                                createKline(new BigDecimal("101"), new BigDecimal("107"), new BigDecimal("85")),
                                createKline(new BigDecimal("103"), new BigDecimal("116"), new BigDecimal("80"))
                        ),
                        List.of(
                                new BigDecimal("18.00000"), // TR K2: max(108-90=18, abs.108-100=8, abs.90-100=10) = 18
                                new BigDecimal("22.00000"), // TR K3: max(107-85=22, abs.107-102=5, abs.85-102=17) = 22
                                new BigDecimal("36.00000")  // TR K4: max(116-80=36, abs.116-101=15, abs.80-101=21) = 36
                        )
                ),
                Arguments.of(
                        "True Range Calculation With All Equal Differences expect low-prevClose",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("100")),
                                createKline(new BigDecimal("100"), new BigDecimal("110"), new BigDecimal("100"))
                        ),
                        List.of(
                                new BigDecimal("10.00000") // TR K2: max(110-100=10, abs.110-100=10, abs.100-100=0) = 10
                        )
                ),
                Arguments.of(
                        "True Range Calculation With Decreasing Prices by same value",
                        "ROYAL_COIN",
                        "1h",
                        List.of(
                                createKline( new BigDecimal("200"), new BigDecimal("210"), new BigDecimal("190")),
                                createKline( new BigDecimal("195"), new BigDecimal("205"), new BigDecimal("185")),
                                createKline(new BigDecimal("190"), new BigDecimal("200"), new BigDecimal("180"))
                        ),
                        List.of(
                                new BigDecimal("20.00000"), // TR K2: max(205-185=20, abs.205-200=5, abs.185-200=15) = 20
                                new BigDecimal("20.00000")  // TR K3: max(200-180=20, abs.200-195=5, abs.180-195=15) = 20
                        )
                )
        );
    }

    private static HistoricalKline createKline(BigDecimal closePrice, BigDecimal highPrice, BigDecimal lowPrice) {
        return HistoricalKline.builder()
                .closePrice(closePrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .build();
    }
}
