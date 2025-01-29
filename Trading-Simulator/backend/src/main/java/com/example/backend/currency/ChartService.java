package com.example.backend.currency;

import com.example.backend.exceptions.CurrencyNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChartService {

    private final HistoricalKlineRepository historicalKlineRepository;
    private final CurrencyRepository currencyRepository;

    public List<HistoricalKlineDTO> getChartData(String symbol, String timeInterval) {
        Optional<Currency> currencyOpt = currencyRepository.findBySymbol(symbol.toUpperCase());
        if (currencyOpt.isEmpty()) {
            throw new CurrencyNotFoundException("Currency not found: " + symbol);
        }

        List<HistoricalKline> klines = historicalKlineRepository.findByCurrencyAndTimeIntervalOrderByOpenTimeAsc(
                currencyOpt.get(), timeInterval);

        return klines.stream()
                .map(HistoricalKlineDTO::new)
                .collect(Collectors.toList());
    }
}
