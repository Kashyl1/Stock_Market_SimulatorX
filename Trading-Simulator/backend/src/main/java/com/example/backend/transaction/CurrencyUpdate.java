package com.example.backend.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CurrencyUpdate {
    private String symbol;
    private BigDecimal price;
}
