package com.example.backend.currency;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CurrencyResponse {
    private String symbol;
    private BigDecimal lastPrice;
    private BigDecimal priceChange;
    private BigDecimal priceChangePercent;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal volume;
}
