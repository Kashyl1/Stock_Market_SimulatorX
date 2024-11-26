package com.example.backend.config;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Tag(name = "Web Client Configuration", description = "Configuration for binance and coingecko requests")
public class WebClientConfig {
    @Bean
    @Qualifier("binanceClient")
    public WebClient binanceClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.binance.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    @Qualifier("coingeckoClient")
    public WebClient coingeckoClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.coingecko.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}


