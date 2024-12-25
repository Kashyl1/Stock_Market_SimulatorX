package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class NegativeDMCalculator implements IndicatorCalculator<List<BigDecimal>> {
    @Override
    public List<BigDecimal> calculate(List<HistoricalKline> klines) {
        List<BigDecimal> negativeDMs = new ArrayList<>();
        for (int i = 1; i < klines.size(); i++) {
            BigDecimal high = klines.get(i).getHighPrice();
            BigDecimal low = klines.get(i).getLowPrice();
            BigDecimal highPrev = klines.get(i - 1).getHighPrice();
            BigDecimal lowPrev = klines.get(i - 1).getLowPrice();

            BigDecimal diffHigh = high.subtract(highPrev).setScale(5, RoundingMode.HALF_UP);
            BigDecimal diffLow = lowPrev.subtract(low).setScale(5, RoundingMode.HALF_UP);

            BigDecimal negativeDM = (diffLow.abs().compareTo(diffHigh.abs()) > 0
            && diffLow.compareTo(BigDecimal.ZERO) > 0)
            ? diffLow
            : BigDecimal.ZERO;
            negativeDMs.add(negativeDM.setScale(5, RoundingMode.HALF_UP));
        }
        return negativeDMs;
    }
}
