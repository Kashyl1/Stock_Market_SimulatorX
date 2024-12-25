package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class TrueRangeCalculator implements IndicatorCalculator<List<BigDecimal>> {
    @Override
    public List<BigDecimal> calculate(List<HistoricalKline> klines) {
        List<BigDecimal> trueRange = new ArrayList<>();
        for (int i = 1; i < klines.size(); i++) {
            BigDecimal high = klines.get(i).getHighPrice();
            BigDecimal low = klines.get(i).getLowPrice();
            BigDecimal closePrev = klines.get(i - 1).getClosePrice();

            BigDecimal tr = high.subtract(low)
                    .max(high.subtract(closePrev).abs())
                    .max(low.subtract(closePrev).abs())
                    .setScale(5, RoundingMode.HALF_UP);
            trueRange.add(tr);
        }
        return trueRange;
    }
}
