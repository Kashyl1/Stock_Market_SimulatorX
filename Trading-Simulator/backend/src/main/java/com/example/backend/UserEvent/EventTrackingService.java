package com.example.backend.UserEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventTrackingService {

    private final UserEventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public void logEvent(String email, UserEvent.EventType eventType, Map<String, Object> details) {
        try {
            String detailsJson = convertMapToJson(details);
            UserEvent event = UserEvent.builder()
                    .email(email)
                    .eventType(eventType)
                    .eventTime(LocalDateTime.now())
                    .details(detailsJson)
                    .build();
            eventRepository.save(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertMapToJson(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public void logEvent(String email, UserEvent.EventType eventType) {
        logEvent(email, eventType, null);
    }
}
