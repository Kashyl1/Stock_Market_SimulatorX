package com.example.backend.admin;

import com.example.backend.alert.AlertService;
import com.example.backend.alert.mail.EmailAlertDTO;
import com.example.backend.alert.trade.TradeAlertDTO;
import com.example.backend.exceptions.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/admin/alerts")
@RequiredArgsConstructor
@Tag(name = "Admin Alert Controller", description = "Management of email and trade alerts")
public class AdminAlertController {

    private final AlertService alertService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email-alerts")
    @Operation(summary = "Get all email alerts", description = "Retrieve a paginated list of all email alerts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email alerts retrieved successfully")
    })
    public ResponseEntity<Page<EmailAlertDTO>> getAllEmailAlerts(Pageable pageable) {
        Page<EmailAlertDTO> emailAlerts = alertService.getAllEmailAlerts(pageable);
        return ResponseEntity.ok(emailAlerts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trade-alerts")
    @Operation(summary = "Get all trade alerts", description = "Retrieve a paginated list of all trade alerts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade alerts retrieved successfully")
    })
    public ResponseEntity<Page<TradeAlertDTO>> getAllTradeAlerts(Pageable pageable) {
        Page<TradeAlertDTO> tradeAlerts = alertService.getAllTradeAlerts(pageable);
        return ResponseEntity.ok(tradeAlerts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/email-alerts/{alertId}")
    @Operation(summary = "Delete an email alert", description = "Delete an email alert by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email alert deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Email alert not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteEmailAlert(
            @Parameter(description = "ID of the email alert to delete") @PathVariable Integer alertId) {
        alertService.deleteEmailAlertById(alertId);
        return ResponseEntity.ok("Email alert has been deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/trade-alerts/{alertId}")
    @Operation(summary = "Delete a trade alert", description = "Delete a trade alert by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trade alert deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trade alert not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteTradeAlert(
            @Parameter(description = "ID of the trade alert to delete") @PathVariable Integer alertId) {
        alertService.deleteTradeAlertById(alertId);
        return ResponseEntity.ok("Trade alert has been deleted");
    }
}
