package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.currency.HistoricalKlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
public class IndicatorSchedulerTest {

    @SpyBean
    private IndicatorCacheService indicatorCacheService;

    @Autowired
    private IndicatorScheduler indicatorScheduler;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private HistoricalKlineRepository historicalKlineRepository;

    @BeforeEach
    void setUp() {
        historicalKlineRepository.deleteAll();
        currencyRepository.deleteAll();

        Currency currency = new Currency();
        currency.setSymbol("BTC");
        currency.setName("Litecoin?");
        currencyRepository.save(currency);

        for (long i = 1; i <= 30; i++) {
            createHistoricalKline(currency, "5m", i, 100 + i, 105 + i, 95 + i, 100 + i, 1000L * i);
        }
    }

    private void createHistoricalKline(Currency currency, String timeInterval, Long openTime,
                                       double openPrice, double highPrice, double lowPrice, double closePrice,
                                       long closeTime) {
        HistoricalKline kline = HistoricalKline.builder()
                .currency(currency)
                .openTime(openTime)
                .openPrice(BigDecimal.valueOf(openPrice))
                .highPrice(BigDecimal.valueOf(highPrice))
                .lowPrice(BigDecimal.valueOf(lowPrice))
                .closePrice(BigDecimal.valueOf(closePrice))
                .volume(BigDecimal.valueOf(100))
                .closeTime(closeTime)
                .timeInterval(timeInterval)
                .build();

        historicalKlineRepository.save(kline);
    }

    @Test
    void testSchedulerUpdatesIndicators() {
        indicatorScheduler.updateIndicators();

        Mockito.verify(indicatorCacheService, Mockito.atLeastOnce()).saveSma(
                Mockito.eq("BTC"), Mockito.eq("5m"), Mockito.anyInt(), Mockito.any(BigDecimal.class)
        );
    }
}

