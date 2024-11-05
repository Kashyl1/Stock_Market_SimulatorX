package com.example.backend.alert;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/create")
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody CreateAlertRequest request) {
        AlertResponse response = alertService.createAlert(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-alerts")
    public ResponseEntity<List<AlertResponse>> getUserAlerts() {
        List<AlertResponse> alerts = alertService.getUserAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/deactivate/{alertId}")
    public ResponseEntity<String> deactivateAlert(@PathVariable Integer alertId) {
        alertService.deactivateAlert(alertId);
        return ResponseEntity.ok("Alert has been deactivated.");
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<String> deleteAlert(@PathVariable Integer alertId) {
        alertService.deleteAlert(alertId);
        return ResponseEntity.ok("Alert has been deleted.");
    }
}