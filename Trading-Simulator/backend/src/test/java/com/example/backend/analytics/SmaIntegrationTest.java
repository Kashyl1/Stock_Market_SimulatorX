package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.currency.HistoricalKlineRepository;
import com.example.backend.exceptions.CurrencyNotFoundException;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.transaction.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
public class SmaIntegrationTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private HistoricalKlineRepository historicalKlineRepository;

    @Autowired
    private PortfolioAssetRepository portfolioAssetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        historicalKlineRepository.deleteAll();
        portfolioAssetRepository.deleteAll();
        transactionRepository.deleteAll();
        currencyRepository.deleteAll();
    }

    @Test
    void testCalculateSmaFromDatabaseUsingCalculateIndicator() {
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("toMarka");
        currency.setCurrencyid(2);
        currencyRepository.save(currency);

        Assertions.assertTrue(currencyRepository.findById(currency.getCurrencyid()).isPresent(),
                "Currency should be saved in the database");

        createHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);
        createHistoricalKline(currency, "1h", 3L, 30, 35, 29, 30, 3000L);
        createHistoricalKline(currency, "1h", 4L, 40, 45, 39, 40, 4000L);
        createHistoricalKline(currency, "1h", 5L, 50, 55, 49, 50, 5000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new SmaCalculator(periods));
            BigDecimal expected = BigDecimal.valueOf(40.0);
            Assertions.assertEquals(0, result.compareTo(expected),
                    "Calculated SMA does not match expected value.");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
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

        kline.setVersion(0L);

        historicalKlineRepository.save(kline);
    }

    @Test
    void testCalculateSmaWithNoData() { // periods > klines
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("No Data Marka");
        currencyRepository.save(currency);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new SmaCalculator(periods));
            Assertions.fail("Should have thrown an exception due to no data available");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for SMA calculation"));
        }
    }

    @Test
    void testCalculateSmaWithFewerCandlesThanPeriod() {
        // 2 klines < 3 periods no nie, tak sie nie bawimy
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("Too Few Marki");
        currencyRepository.save(currency);

        createHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);

        int periods = 3;
        try {
            analyticsService.calculateIndicator("ROYAL_COIN", "1h", new SmaCalculator(periods));
            Assertions.fail("Expected exception due to not enough data");
        } catch (IllegalArgumentException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for SMA calculation"));
        }
    }

    @Test
    void testCalculateSmaWithOneCandle() {
        // jo je git
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("One Candle Marka");
        currencyRepository.save(currency);

        createHistoricalKline(currency, "1h", 1L, 50, 55, 45, 50, 1000L);

        // SMA dla 1 świecy z okresem 1 = po prostu cena zamknięcia tej świecy = 50
        int periods = 1;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new SmaCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(50.0)),
                    "SMA should match the single candle close price");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateSmaAllSamePrice() {
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("Same Price Marka");
        currencyRepository.save(currency);

        double closePrice = 100.0;
        createHistoricalKline(currency, "1h", 1L, 100, 105, 95, closePrice, 1000L);
        createHistoricalKline(currency, "1h", 2L, 100, 105, 95, closePrice, 2000L);
        createHistoricalKline(currency, "1h", 3L, 100, 105, 95, closePrice, 3000L);
        createHistoricalKline(currency, "1h", 4L, 100, 105, 95, closePrice, 4000L);
        createHistoricalKline(currency, "1h", 5L, 100, 105, 95, closePrice, 5000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new SmaCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(100.0)),
                    "SMA should match the same close price of all candles");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateSmaDifferentIntervalAndMoreCandles() {
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("Fast Interval Marka");
        currencyRepository.save(currency);

        createHistoricalKline(currency, "5m", 1L, 10, 15, 9, 10, 1000L);
        createHistoricalKline(currency, "5m", 2L, 15, 20, 14, 15, 2000L);
        createHistoricalKline(currency, "5m", 3L, 20, 25, 19, 20, 3000L);
        createHistoricalKline(currency, "5m", 4L, 25, 30, 24, 25, 4000L);
        createHistoricalKline(currency, "5m", 5L, 30, 35, 29, 30, 5000L);
        createHistoricalKline(currency, "5m", 6L, 35, 40, 34, 35, 6000L);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "5m", new SmaCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(30.0)),
                    "SMA should be 30.0 for the last three candles");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }
}
