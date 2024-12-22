package com.example.backend.analytics;

import com.example.backend.currency.*;
import com.example.backend.exceptions.CurrencyNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class AnalyticsService {

    private final HistoricalKlineRepository historicalKlineRepository;
    private final CurrencyRepository currencyRepository;

    public BigDecimal calculateIndicator(String symbol, String timeInterval, IndicatorCalculator calculator) {
        Currency currency = currencyRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found: " + symbol));

        List<HistoricalKline> klines = historicalKlineRepository.findByCurrencyAndTimeIntervalOrderByOpenTimeAsc(currency, timeInterval);

        return calculator.calculate(klines);
    }
}

