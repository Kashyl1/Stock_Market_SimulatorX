package com.example.backend.currency;

import com.example.backend.exceptions.CurrencyNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/charts")
@RequiredArgsConstructor
@Tag(name = "Chart Controller", description = "Provides endpoints to retrieve chart data")
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/{symbol}/{interval}")
    @Operation(summary = "Get chart data", description = "Retrieves historical kline data for a given symbol and interval")
    public ResponseEntity<List<HistoricalKlineDTO>> getChartData(
            @PathVariable String symbol,
            @PathVariable String interval) {
            List<HistoricalKlineDTO> klinesDTO = chartService.getChartData(symbol, interval);
            return ResponseEntity.ok(klinesDTO);
    }
}
