package com.example.backend.admin;

import com.example.backend.alert.AlertService;
import com.example.backend.alert.mail.EmailAlertDTO;
import com.example.backend.alert.trade.TradeAlertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/alerts")
@RequiredArgsConstructor
public class AdminAlertController {

    private final AlertService alertService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/email-alerts")
    public ResponseEntity<Page<EmailAlertDTO>> getAllEmailAlerts(Pageable pageable) {
        Page<EmailAlertDTO> emailAlerts = alertService.getAllEmailAlerts(pageable);
        return ResponseEntity.ok(emailAlerts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/trade-alerts")
    public ResponseEntity<Page<TradeAlertDTO>> getAllTradeAlerts(Pageable pageable) {
        Page<TradeAlertDTO> tradeAlerts = alertService.getAllTradeAlerts(pageable);
        return ResponseEntity.ok(tradeAlerts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/email-alerts/{alertId}")
    public ResponseEntity<String> deleteEmailAlert(@PathVariable Integer alertId) {
        alertService.deleteEmailAlertById(alertId);
        return ResponseEntity.ok("Email alert has been deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/trade-alerts/{alertId}")
    public ResponseEntity<String> deleteTradeAlert(@PathVariable Integer alertId) {
        alertService.deleteTradeAlertById(alertId);
        return ResponseEntity.ok("Trade alert has been deleted");
    }
}
