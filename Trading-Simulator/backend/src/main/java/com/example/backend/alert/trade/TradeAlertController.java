package com.example.backend.alert.trade;

import com.example.backend.exceptions.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/alerts/trade")
@RequiredArgsConstructor
@Tag(name = "Trade Alert Controller", description = "Endpoints for managing trade alerts")
public class TradeAlertController {

    private final TradeAlertService tradeAlertService;

    @PostMapping("/create")
    @Operation(summary = "Create a new trade alert", description = "Creates a new trade alert for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade alert created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TradeAlertResponse> createTradeAlert(
            @RequestBody @Valid CreateTradeAlertRequest request) {
        TradeAlert tradeAlert = tradeAlertService.createTradeAlert(request);
        TradeAlertResponse response = TradeAlertResponse.fromTradeAlert(tradeAlert);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/deactivate/{tradeAlertId}")
    @Operation(summary = "Deactivate a trade alert", description = "Deactivates a specific trade alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade alert deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Trade alert not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deactivateTradeAlert(
            @Parameter(description = "ID of the trade alert to deactivate") @PathVariable Integer tradeAlertId) {
        tradeAlertService.deactivateTradeAlert(tradeAlertId);
        return ResponseEntity.ok("Trade alert has been deactivated.");
    }

    @DeleteMapping("/{tradeAlertId}")
    @Operation(summary = "Delete a trade alert", description = "Deletes a specific trade alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade alert deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trade alert not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteTradeAlert(
            @Parameter(description = "ID of the trade alert to delete") @PathVariable Integer tradeAlertId) {
        tradeAlertService.deleteTradeAlert(tradeAlertId);
        return ResponseEntity.ok("Trade alert has been deleted.");
    }

    @GetMapping("/my-trade-alerts")
    @Operation(summary = "Get user's trade alerts", description = "Retrieves all trade alerts for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade alerts retrieved successfully")
    })
    public ResponseEntity<List<TradeAlertResponse>> getUserTradeAlerts() {
        List<TradeAlert> tradeAlerts = tradeAlertService.getUserTradeAlerts();
        List<TradeAlertResponse> responses = tradeAlerts.stream()
                .map(TradeAlertResponse::fromTradeAlert)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
