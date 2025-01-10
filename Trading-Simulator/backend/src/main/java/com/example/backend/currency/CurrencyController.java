package com.example.backend.currency;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
@Tag(name = "Currency Controller", description = "Provides endpoints to retrieve data about currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(
            summary = "Get current price of a currency by symbol",
            description = "Returns the current market price of the requested currency"
    )
    @GetMapping("/current-price/{symbol}")
    public BigDecimal getCurrentPrice(@PathVariable String symbol) {
        return currencyService.getCurrentPrice(symbol);
    }
}
