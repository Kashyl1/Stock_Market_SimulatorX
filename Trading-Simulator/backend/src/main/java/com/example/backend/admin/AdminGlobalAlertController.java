package com.example.backend.admin;

import com.example.backend.alert.global.GlobalAlert;
import com.example.backend.alert.global.GlobalAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/global-alerts")
@RequiredArgsConstructor
public class AdminGlobalAlertController {

    private final GlobalAlertService globalAlertService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GlobalAlert> createGlobalAlert(@RequestBody GlobalAlert globalAlert) {
        GlobalAlert createdAlert = globalAlertService.createGlobalAlert(globalAlert);
        return ResponseEntity.ok(createdAlert);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<GlobalAlert>> getAllGlobalAlerts() {
        List<GlobalAlert> alerts = globalAlertService.getAllGlobalAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{globalAlertId}")
    public ResponseEntity<String> deleteGlobalAlert(@PathVariable Integer globalAlertId) {
        globalAlertService.deleteGlobalAlertById(globalAlertId);
        return ResponseEntity.ok("Global alert has been deleted");
    }
}
