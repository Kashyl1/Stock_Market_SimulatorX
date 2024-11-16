package com.example.backend.alert.global;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalAlertService {

    private final GlobalAlertRepository globalAlertRepository;

    @Transactional
    public GlobalAlert createGlobalAlert(GlobalAlert globalAlert) {
        globalAlert.setCreatedAt(LocalDateTime.now());
        globalAlert.setActive(true);
        return globalAlertRepository.save(globalAlert);
    }

    public List<GlobalAlert> getAllGlobalAlerts() {
        return globalAlertRepository.findAll();
    }

    @Transactional
    public void deleteGlobalAlertById(Integer globalAlertId) {
        globalAlertRepository.deleteById(globalAlertId);
    }
}
