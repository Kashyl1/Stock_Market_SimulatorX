package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class MacdCalculator implements IndicatorCalculator<MacdResult> {

    private static final int SHORT_PERIODS = 12;
    private static final int LONG_PERIODS = 26;
    private static final int SIGNAL_PERIODS = 9;
    private static final Logger logger = LoggerFactory.getLogger(MacdCalculator.class);

    @Override
    public MacdResult calculate(List<HistoricalKline> klines) {
        if (klines.size() < LONG_PERIODS + SIGNAL_PERIODS) {
            throw new NotEnoughDataForCalculationException("Not enough data for MACD calculator");
        }

        EmaCalculator emaShortCalculator = new EmaCalculator(SHORT_PERIODS);
        List<BigDecimal> emaShortSeries = emaShortCalculator.calculate(klines);

        EmaCalculator emaLongCalculator = new EmaCalculator(LONG_PERIODS);
        List<BigDecimal> emaLongSeries = emaLongCalculator.calculate(klines);

        List<BigDecimal> macdSeries = getBigDecimals(klines, emaShortSeries, emaLongSeries);

        if (macdSeries.size() < SIGNAL_PERIODS) {
            throw new NotEnoughDataForCalculationException("Not enough MACD data for signal line calculator");
        }

        EmaCalculator signalEmaCalculator = new EmaCalculator(SIGNAL_PERIODS);
        List<BigDecimal> signalSeries = signalEmaCalculator.calculateEmaFromValues(macdSeries);

        BigDecimal latestMacd = macdSeries.get(macdSeries.size() - 1);
        BigDecimal latestSignal = signalSeries.get(signalSeries.size() - 1);

        latestMacd = latestMacd.setScale(8, RoundingMode.HALF_UP);
        latestSignal = latestSignal.setScale(8, RoundingMode.HALF_UP);

        return new MacdResult(latestMacd, latestSignal);
    }

    private static List<BigDecimal> getBigDecimals(List<HistoricalKline> klines, List<BigDecimal> emaShortSeries, List<BigDecimal> emaLongSeries) {
        int emaShortStartIndex = SHORT_PERIODS - 1;
        int emaLongStartIndex = LONG_PERIODS - 1;

        List<BigDecimal> macdSeries = new ArrayList<>();
        int end = klines.size();

        for (int i = emaLongStartIndex; i < end; i++) {
            BigDecimal emaShort = emaShortSeries.get(i - emaShortStartIndex);
            BigDecimal emaLong = emaLongSeries.get(i - emaLongStartIndex);
            BigDecimal macd = emaShort.subtract(emaLong);
            macdSeries.add(macd);
        }
        return macdSeries;
    }
}
