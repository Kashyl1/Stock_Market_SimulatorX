package com.example.backend.adminEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminEventTrackingService {

    private final AdminEventRepository adminEventRepository;
    private final ObjectMapper objectMapper;

    public void logEvent(String adminEmail, AdminEvent.EventType eventType, Map<String, Object> details) {
        try {
            String detailsJson = convertMapToJson(details);
            AdminEvent event = AdminEvent.builder()
                    .adminEmail(adminEmail)
                    .eventType(eventType)
                    .eventTime(LocalDateTime.now())
                    .details(detailsJson)
                    .build();
            adminEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertMapToJson(Map<String, Object> map) {
        if (map == null) {
            return  null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void logEvent(String adminEmail, AdminEvent.EventType eventType) {
        logEvent(adminEmail, eventType, null);
    }
}
