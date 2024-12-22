package com.example.backend.analytics;

import com.example.backend.currency.Currency;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.currency.HistoricalKline;
import com.example.backend.currency.HistoricalKlineRepository;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected CurrencyRepository currencyRepository;

    @Autowired
    protected HistoricalKlineRepository historicalKlineRepository;

    @Autowired
    protected PortfolioAssetRepository portfolioAssetRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    @Autowired
    protected AnalyticsService analyticsService;

    @BeforeEach
    void setUpDatabase() {
        historicalKlineRepository.deleteAll();
        portfolioAssetRepository.deleteAll();
        transactionRepository.deleteAll();
        currencyRepository.deleteAll();
    }

    protected Currency createAndSaveCurrency(String symbol, String name) {
        Currency currency = new Currency();
        currency.setSymbol(symbol);
        currency.setName(name);
        return currencyRepository.save(currency);
    }

    protected void createAndSaveHistoricalKline(Currency currency, String timeInterval, Long openTime,
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
}
