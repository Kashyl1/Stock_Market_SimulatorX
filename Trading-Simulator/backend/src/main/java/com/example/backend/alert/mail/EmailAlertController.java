package com.example.backend.alert.mail;

import com.example.backend.exceptions.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/alerts/email")
@RequiredArgsConstructor
@Tag(name = "Email Alert Controller", description = "Endpoints for managing email alerts")
public class EmailAlertController {

    private final EmailAlertService emailAlertService;

    @PostMapping("/create")
    @Operation(summary = "Create a new email alert", description = "Creates a new email alert for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email alert created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<EmailAlertResponse> createAlert(
            @RequestBody @Valid CreateEmailAlertRequest request) {
        EmailAlertResponse response = emailAlertService.createAlert(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-alerts")
    @Operation(summary = "Get user's email alerts", description = "Retrieves all email alerts for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email alerts retrieved successfully")
    })
    public ResponseEntity<List<EmailAlertResponse>> getUserAlerts() {
        List<EmailAlertResponse> alerts = emailAlertService.getUserAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/deactivate/{alertId}")
    @Operation(summary = "Deactivate an email alert", description = "Deactivates a specific email alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email alert deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "Email alert not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deactivateAlert(
            @Parameter(description = "ID of the email alert to deactivate") @PathVariable Integer alertId) {
        emailAlertService.deactivateAlert(alertId);
        return ResponseEntity.ok("Email alert has been deactivated.");
    }

    @DeleteMapping("/{alertId}")
    @Operation(summary = "Delete an email alert", description = "Deletes a specific email alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email alert deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Email alert not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteAlert(
            @Parameter(description = "ID of the email alert to delete") @PathVariable Integer alertId) {
        emailAlertService.deleteAlert(alertId);
        return ResponseEntity.ok("Email alert has been deleted.");
    }
}
