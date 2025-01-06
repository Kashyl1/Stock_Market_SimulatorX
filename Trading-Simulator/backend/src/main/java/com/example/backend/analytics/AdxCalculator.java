package com.example.backend.analytics;

import com.example.backend.currency.HistoricalKline;
import com.example.backend.exceptions.NotEnoughDataForCalculationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class AdxCalculator implements IndicatorCalculator<BigDecimal> {

    private static final int PERIODS = 14;

    private final TrueRangeCalculator trueRangeCalculator;
    private final PositiveDMCalculator positiveDMCalculator;
    private final NegativeDMCalculator negativeDMCalculator;
    private final WilderSmoothingCalculator wilderSmoothingCalculator;
    private final DirectionalIndicatorCalculator directionalIndicatorCalculator;
    private final DXCalculator dxCalculator;

    public AdxCalculator(
            TrueRangeCalculator trueRangeCalculator,
            PositiveDMCalculator positiveDMCalculator,
            NegativeDMCalculator negativeDMCalculator,
            WilderSmoothingCalculator wilderSmoothingCalculator,
            DirectionalIndicatorCalculator directionalIndicatorCalculator,
            DXCalculator dxCalculator) {
        this.trueRangeCalculator = trueRangeCalculator;
        this.positiveDMCalculator = positiveDMCalculator;
        this.negativeDMCalculator = negativeDMCalculator;
        this.wilderSmoothingCalculator = wilderSmoothingCalculator;
        this.directionalIndicatorCalculator = directionalIndicatorCalculator;
        this.dxCalculator = dxCalculator;
    }

    @Override
    public BigDecimal calculate(List<HistoricalKline> klines) {
        if (klines.size() < PERIODS * 2 + 1) {
            throw new NotEnoughDataForCalculationException("Not enough ADX data to calculate");
        }
        // WZORY NA WSZYSTKO: EXCEL ADX_KALKULATOR
        // Liczymy TR, +-DM
        List<BigDecimal> trueRanges = trueRangeCalculator.calculate(klines);
        List<BigDecimal> positiveDMs = positiveDMCalculator.calculate(klines);
        List<BigDecimal> negativeDMs = negativeDMCalculator.calculate(klines);

        // liczymy wygładzenie, wygładzone  +-DM
        List<BigDecimal> smoothedTR = wilderSmoothingCalculator.calculate(trueRanges);
        List<BigDecimal> smoothedPositiveDM = wilderSmoothingCalculator.calculate(positiveDMs);
        List<BigDecimal> smoothedNegativeDM = wilderSmoothingCalculator.calculate(negativeDMs);

        // Liczymy +-DI
        List<BigDecimal> positiveDI = directionalIndicatorCalculator.calculate(smoothedPositiveDM, smoothedTR);
        List<BigDecimal> negativeDI = directionalIndicatorCalculator.calculate(smoothedNegativeDM, smoothedTR);

        // DX
        List<BigDecimal> dxValues = dxCalculator.calculate(positiveDI, negativeDI);

        // ADX (z czego interere ostatni)
        List<BigDecimal> adxValues = wilderSmoothingCalculator.calculate(dxValues);

        return adxValues.get(adxValues.size() - 1);
    }
}