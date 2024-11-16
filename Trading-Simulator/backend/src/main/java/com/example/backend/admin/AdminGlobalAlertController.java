package com.example.backend.admin;

import com.example.backend.alert.global.GlobalAlert;
import com.example.backend.alert.global.GlobalAlertService;
import com.example.backend.exceptions.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/admin/global-alerts")
@RequiredArgsConstructor
@Tag(name = "Admin Global Alert Controller", description = "Management of global alerts")
public class AdminGlobalAlertController {

    private final GlobalAlertService globalAlertService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a global alert", description = "Create a new global alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Global alert created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<GlobalAlert> createGlobalAlert(@RequestBody GlobalAlert globalAlert) {
        GlobalAlert createdAlert = globalAlertService.createGlobalAlert(globalAlert);
        return ResponseEntity.ok(createdAlert);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all global alerts", description = "Retrieve a list of all global alerts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Global alerts retrieved successfully")
    })
    public ResponseEntity<List<GlobalAlert>> getAllGlobalAlerts() {
        List<GlobalAlert> alerts = globalAlertService.getAllGlobalAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{globalAlertId}")
    @Operation(summary = "Delete a global alert", description = "Delete a global alert by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Global alert deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Global alert not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteGlobalAlert(
            @Parameter(description = "ID of the global alert to delete") @PathVariable Integer globalAlertId) {
        globalAlertService.deleteGlobalAlertById(globalAlertId);
        return ResponseEntity.ok("Global alert has been deleted");
    }
}
