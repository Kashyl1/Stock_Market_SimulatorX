package com.example.backend.alert.global;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/global-alert")
public class GlobalAlertController {

    private final GlobalAlertService globalAlertService;

    @GetMapping
    public ResponseEntity<GlobalAlert> getActiveGlobalAlert() {
        GlobalAlert globalAlert = globalAlertService.getNewestActiveGlobalAlert();

        if (globalAlert == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(globalAlert);
    }
}
