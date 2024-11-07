package com.example.backend.alert.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/alerts/email")
@RequiredArgsConstructor
public class EmailAlertController {

    private final EmailAlertService emailAlertService;

    @PostMapping("/create")
    public ResponseEntity<EmailAlertResponse> createAlert(@Valid @RequestBody CreateEmailAlertRequest request) {
        EmailAlertResponse response = emailAlertService.createAlert(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-alerts")
    public ResponseEntity<List<EmailAlertResponse>> getUserAlerts() {
        List<EmailAlertResponse> alerts = emailAlertService.getUserAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/deactivate/{alertId}")
    public ResponseEntity<String> deactivateAlert(@PathVariable Integer alertId) {
        emailAlertService.deactivateAlert(alertId);
        return ResponseEntity.ok("Alert has been deactivated.");
    }

    @DeleteMapping("/{alertId}")
    public ResponseEntity<String> deleteAlert(@PathVariable Integer alertId) {
        emailAlertService.deleteAlert(alertId);
        return ResponseEntity.ok("Alert has been deleted.");
    }
}