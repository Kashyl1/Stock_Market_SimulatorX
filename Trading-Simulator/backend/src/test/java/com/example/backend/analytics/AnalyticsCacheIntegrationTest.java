package com.example.backend.analytics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AnalyticsCacheIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private IndicatorCacheService indicatorCacheService;

    @Test
    void testSmaEndpointWithRedisCache() {
        indicatorCacheService.saveSma("ROYAL_COIN", "1h", 3, new BigDecimal("40.00"));

        webTestClient.get()
                .uri("/api/analytics/sma/ROYAL_COIN/1h/3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> {
                    Assertions.assertEquals(new BigDecimal("40.00"), value);
                });
    }

    @Test
    void testSaveAndGetSmaDirectly() {
        String symbol = "ROYAL_COIN";
        String interval = "1h";
        int periods = 14;
        BigDecimal expectedValue = new BigDecimal("40.00");

        indicatorCacheService.saveSma(symbol, interval, periods, expectedValue);
        BigDecimal actualValue = indicatorCacheService.getSma(symbol, interval, periods);

        Assertions.assertNotNull(actualValue, "SMA value should not be null");
        Assertions.assertEquals(expectedValue, actualValue, "SMA Value should match expected value");
    }

    @Test
    void testEmaEndpointWithRedisCache() {
        indicatorCacheService.saveEma("ROYAL_COIN", "1h", 14, new BigDecimal("40.00"));

        webTestClient.get()
                .uri("/api/analytics/ema/ROYAL_COIN/1h/14")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> {
                    Assertions.assertEquals(new BigDecimal("40.00"), value);
                });
    }

    @Test
    void testSaveAndGetEmaDirectly() {
        String symbol = "ROYAL_COIN";
        String interval = "1h";
        int periods = 14;
        BigDecimal expectedValue = new BigDecimal("40.00");

        indicatorCacheService.saveEma(symbol, interval, periods, expectedValue);
        BigDecimal actualValue = indicatorCacheService.getEma(symbol, interval, periods);

        Assertions.assertNotNull(actualValue, "EMA value should not be null");
        Assertions.assertEquals(expectedValue, actualValue, "EMA value should match expected value");
    }

    @Test
    void testRsiEndpointWithRedisCache() {
        indicatorCacheService.saveRsi("ROYAL_COIN", "1h", 14, new BigDecimal("40.00"));

        webTestClient.get()
                .uri("/api/analytics/rsi/ROYAL_COIN/1h/14")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> {
                    Assertions.assertEquals(new BigDecimal("40.00"), value);
                });
    }

    @Test
    void testSaveAndGetRsiDirectly() {
        String symbol = "ROYAL_COIN";
        String interval = "1h";
        int periods = 14;
        BigDecimal expectedValue = new BigDecimal("40.00");

        indicatorCacheService.saveRsi(symbol, interval, periods, expectedValue);
        BigDecimal actualValue = indicatorCacheService.getRsi(symbol, interval, periods);

        Assertions.assertNotNull(actualValue, "RSI value should not be null");
        Assertions.assertEquals(expectedValue, actualValue, "RSI value should match expected value");
    }

    @Test
    void testVolatilityEndpointWithRedisCache() {
        indicatorCacheService.saveVolatility("ROYAL_COIN", "1h", 14, new BigDecimal("40.00"));

        webTestClient.get()
                .uri("/api/analytics/volatility/ROYAL_COIN/1h/14")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> {
                    Assertions.assertEquals(new BigDecimal("40.00"), value);
                });
    }

    @Test
    void testSaveAndGetVolatilityDirectly() {
        String symbol = "ROYAL_COIN";
        String interval = "1h";
        int periods = 14;
        BigDecimal expectedValue = new BigDecimal("40.00");

        indicatorCacheService.saveVolatility(symbol, interval, periods, expectedValue);
        BigDecimal actualValue = indicatorCacheService.getVolatility(symbol, interval, periods);

        Assertions.assertNotNull(actualValue, "Volatility value should not be null");
        Assertions.assertEquals(expectedValue, actualValue, "Volatility value should match expected value");
    }

    @Test
    void testMacdEndpointWithRedisCache() {
        MacdResult expectedResult = new MacdResult(new BigDecimal("40.00000000"), new BigDecimal("40.00000000"));
        indicatorCacheService.saveMacd("ROYAL_COIN", "1h", expectedResult);

        webTestClient.get()
                .uri("/api/analytics/macd/ROYAL_COIN/1h")
                .exchange()
                .expectStatus().isOk()
                .expectBody(MacdResult.class)
                .value(value -> {
                    Assertions.assertEquals(expectedResult, value);
                });
    }

    @Test
    void testSaveAndGetMacdDirectly() {
        String symbol = "ROYAL_COIN";
        String interval = "1h";
        MacdResult expectedResult = new MacdResult(new BigDecimal("0.10000000"), new BigDecimal("0.05000000"));

        indicatorCacheService.saveMacd(symbol, interval, expectedResult);
        MacdResult actualResult = indicatorCacheService.getMacd(symbol, interval);

        Assertions.assertNotNull(actualResult, "MACD value should not be null");
        Assertions.assertEquals(expectedResult, actualResult, "MACD value should match expected value");
    }

    @Test
    void testAdxEndpointWithRedisCache() {
        indicatorCacheService.saveAdx("ROYAL_COIN", "1h", new BigDecimal("30.00"));

        webTestClient.get()
                .uri("/api/analytics/adx/ROYAL_COIN/1h")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> {
                    Assertions.assertEquals(new BigDecimal("30.00"), value);
                });
    }

    @Test
    void testSaveAndGetAdxDirectly() {
        String symbol = "ROYAL_COIN";
        String interval = "1h";
        BigDecimal expectedValue = new BigDecimal("30.00");

        indicatorCacheService.saveAdx(symbol, interval, expectedValue);
        BigDecimal actualValue = indicatorCacheService.getAdx(symbol, interval);

        Assertions.assertNotNull(actualValue, "ADX value should not be null");
        Assertions.assertEquals(expectedValue, actualValue, "ADX value should match expected value");
    }

}


