package com.example.backend;

import com.example.backend.analytics.IndicatorCacheService;
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
        // Załóżmy, że scheduler już uruchomił się i wrzucił dane do Redis,
        // albo możesz sam wywołać indicatorCacheService.saveSma(...) żeby zasymulować.
        indicatorCacheService.saveSma("BTC", "1h", 3, new BigDecimal("40.00"));

        // Teraz wykonaj żądanie do endpointu:
        webTestClient.get()
                .uri("/api/analytics/sma/BTC/1h/3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BigDecimal.class)
                .value(value -> {
                    // Sprawdź czy zwróciło poprawną wartość z Redis
                    Assertions.assertEquals(new BigDecimal("40.00"), value);
                });
    }
}

