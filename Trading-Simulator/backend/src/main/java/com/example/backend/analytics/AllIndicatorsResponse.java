package com.example.backend.analytics;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AllIndicatorsResponse {
    private BigDecimal sma;
    private BigDecimal ema;
    private BigDecimal rsi;
    private BigDecimal volatility;
    private MacdResult macd;
    private BigDecimal adx;
    private BigDecimal bp;
    private BigDecimal williamsR;
    private BigDecimal cci;
    private BigDecimal atr;
}
