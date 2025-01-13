package com.example.backend.alert.global;

import com.example.backend.adminEvent.AdminEvent;
import com.example.backend.adminEvent.AdminEventTrackingService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.GlobalAlertNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GlobalAlertService {

    private final GlobalAlertRepository globalAlertRepository;
    private final AdminEventTrackingService adminEventTrackingService;
    private final AuthenticationService authenticationService;

    @Transactional
    public GlobalAlert createGlobalAlert(GlobalAlertResponse request) {
        String adminEmail = authenticationService.getCurrentUserEmail();

        GlobalAlert globalAlert = GlobalAlert.builder()
                .message(request.getMessage() != null ? request.getMessage() : "System update scheduled")
                .scheduledFor(request.getScheduledFor() != null ? request.getScheduledFor() : LocalDateTime.now().plusMinutes(30))
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        GlobalAlert savedAlert = globalAlertRepository.save(globalAlert);


        Map<String, Object> details = Map.of(
                "globalAlertId", savedAlert.getGlobalAlertid(),
                "message", savedAlert.getMessage(),
                "Scheduled_for", savedAlert.getScheduledFor()
        );
        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.CREATE_GLOBAL_ALERT, details);

        return savedAlert;
    }

    public List<GlobalAlert> getAllGlobalAlerts() {
        return globalAlertRepository.findAll();
    }

    @Transactional
    public void deleteGlobalAlertById(Integer globalAlertId) {
        String adminEmail = authenticationService.getCurrentUserEmail();

        GlobalAlert alert = globalAlertRepository.findById(globalAlertId)
                        .orElseThrow(() -> new GlobalAlertNotFoundException("Global alert not found!"));

        globalAlertRepository.deleteById(globalAlertId);

        Map<String, Object> details = Map.of(
                "globalAlertId", globalAlertId
        );
        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.DELETE_GLOBAL_ALERT, details);
    }

    public GlobalAlert getNewestActiveGlobalAlert() {
        GlobalAlert alert = globalAlertRepository.findFirstByActiveTrueOrderByCreatedAtDesc();
        if (alert == null) {
            throw new GlobalAlertNotFoundException("Global alert not found!");
        }
        return alert;
    }
}
