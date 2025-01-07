package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CciCalculator implements IndicatorCalculator<BigDecimal> {

    /*
    The formula for the Commodity Channel Index (CCI)
    CCI is calculated using the following formula:
    CCI = (Typical Price - Simple Moving Average) / (0.015 × Mean Deviation)
    Where:
    Typical Price (TP) = (High + Low + Close) / 3
    Simple Moving Average (SMA) is calculated over a specified period (typically 20 periods).
    Mean Deviation is the average of the absolute differences between the Typical Price and the SMA over the same period.
     */

    private static final BigDecimal CONSTANT = new BigDecimal("0.015");
    private static final int PERIODS = 20;

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {

        if (klines.size() < PERIODS) {
            throw new NotEnoughDataForCalculationException("Not enough data for CCI calculator");
        }

        List<HistoricalKline> subset = klines.subList(klines.size() - PERIODS, klines.size());
        // TP
        BigDecimal sumOfTp = BigDecimal.ZERO;
        for (HistoricalKline kline : subset) {
            BigDecimal tp = kline.getHighPrice()
                    .add(kline.getLowPrice())
                    .add(kline.getClosePrice())
                    .divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);
            sumOfTp = sumOfTp.add(tp);
        }

        BigDecimal sma = sumOfTp.divide(BigDecimal.valueOf(PERIODS), RoundingMode.HALF_UP);

        // Mean deviation
        BigDecimal meanDeviationSum = BigDecimal.ZERO;
        for (HistoricalKline kline : subset) {
            BigDecimal tp = kline.getHighPrice()
                    .add(kline.getLowPrice())
                    .add(kline.getClosePrice())
                    .divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);
            meanDeviationSum = meanDeviationSum.add(tp.subtract(sma).abs());
        }
        BigDecimal meanDeviation = meanDeviationSum.divide(BigDecimal.valueOf(PERIODS), RoundingMode.HALF_UP);

        // CCI
        HistoricalKline lastKline = klines.get(klines.size() - 1);
        BigDecimal lastTp = lastKline.getHighPrice()
                .add(lastKline.getLowPrice())
                .add(lastKline.getClosePrice())
                .divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);

        if (meanDeviation.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return lastTp
                .subtract(sma)
                .divide(CONSTANT.multiply(meanDeviation), RoundingMode.HALF_UP);

    }
}
