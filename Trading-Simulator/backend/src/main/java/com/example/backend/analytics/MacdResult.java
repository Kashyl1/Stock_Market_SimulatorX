package com.example.backend.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class MacdResult {
    private BigDecimal macd;
    private BigDecimal signalLine;

    public MacdResult format(int scale) {
        return new MacdResult(
                macd.setScale(scale, RoundingMode.HALF_UP),
                signalLine.setScale(scale, RoundingMode.HALF_UP)
        );
    }
}
