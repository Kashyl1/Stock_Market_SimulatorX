package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class PositiveDMCalculator implements IndicatorCalculator<List<BigDecimal>> {
    @Override
    public List<BigDecimal> calculate(List<HistoricalKline> klines) {
        List<BigDecimal> positiveDMs = new ArrayList<>();
        for (int i = 1; i < klines.size(); i++) {
            BigDecimal high = klines.get(i).getHighPrice();
            BigDecimal low = klines.get(i).getLowPrice();
            BigDecimal highPrev = klines.get(i - 1).getHighPrice();
            BigDecimal lowPrev = klines.get(i - 1).getLowPrice();

            BigDecimal diffHigh = high.subtract(highPrev).setScale(5, RoundingMode.HALF_UP);
            BigDecimal diffLow = lowPrev.subtract(low).setScale(5, RoundingMode.HALF_UP);

            BigDecimal positiveDM = (diffHigh.abs().compareTo(diffLow.abs()) > 0 && diffHigh.compareTo(BigDecimal.ZERO) > 0)
                    ? diffHigh
                    : BigDecimal.ZERO;
            positiveDMs.add(positiveDM.setScale(5, RoundingMode.HALF_UP));
        }
        return positiveDMs;
    }
}
