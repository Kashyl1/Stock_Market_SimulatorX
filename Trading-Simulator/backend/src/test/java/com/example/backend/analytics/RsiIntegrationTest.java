package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.currency.HistoricalKlineRepository;
import com.example.backend.exceptions.CurrencyNotFoundException;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.transaction.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;

@SpringBootTest
@ActiveProfiles("test")
public class RsiIntegrationTest {

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
    void testCalculateRsiFromDatabaseUsingCalculateIndicator() {
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
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            BigDecimal expected = BigDecimal.valueOf(100);
            Assertions.assertEquals(0, result.compareTo(expected),
                    "Calculated Rsi do not match expected value.");
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
    void testCalculateRsiWithNoData() { // periods > klines
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("No Data Marka");
        currencyRepository.save(currency);

        int periods = 3;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            Assertions.fail("Should have thrown an exception due to no data available");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found.");
        } catch (NotEnoughDataForCalculationException e) {
            Assertions.assertTrue(e.getMessage().contains("Not enough data for RSI calculation"));
        }
    }

    @Test
    void testCalculateRsiWithFewerCandlesThanPeriod() {
        // n klines < m periods no nie, tak sie nie bawimy
        Currency currency = new Currency();
        currency.setSymbol("ROYAL_COIN");
        currency.setName("Too Few Marki");
        currencyRepository.save(currency);

        createHistoricalKline(currency, "1h", 1L, 10, 15, 9, 10, 1000L);
        createHistoricalKline(currency, "1h", 2L, 20, 25, 19, 20, 2000L);

        int periods = 3;
        try {
            analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            Assertions.fail("Expected exception due to not enough data");
        } catch (NotEnoughDataForCalculationException e) {
            System.out.println(e.getMessage());
            Assertions.assertTrue(e.getMessage().contains("Not enough data for RSI calculation"));
        }
    }

    @Test
    void testCalculateRsiAllSamePrice() {
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
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "1h", new RsiCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(100.0)),
                    "Same price = rsi 100");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }

    @Test
    void testCalculateRsiDifferentIntervalAndMoreCandles() {
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

        int periods = 4;
        try {
            BigDecimal result = analyticsService.calculateIndicator("ROYAL_COIN", "5m", new RsiCalculator(periods));
            Assertions.assertEquals(0, result.compareTo(BigDecimal.valueOf(100.0)),
                    "Rsi should be 100, there is no loss cuz 15-10 > 20-15 > 25-20 i tak dalejxd");
        } catch (CurrencyNotFoundException e) {
            Assertions.fail("Currency should be found but got CurrencyNotFoundException");
        }
    }
}
