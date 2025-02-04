package com.example.backend.userEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class UserEventTrackingService {

    private final UserEventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final AtomicLong dailyCount = new AtomicLong(0);
    public void logEvent(String email, UserEvent.EventType eventType, Map<String, Object> details) {
            String detailsJson = convertMapToJson(details);
            UserEvent event = UserEvent.builder()
                    .email(email)
                    .eventType(eventType)
                    .eventTime(LocalDateTime.now())
                    .details(detailsJson)
                    .build();
            eventRepository.save(event);
            if (eventType == UserEvent.EventType.BUY_CRYPTO || eventType == UserEvent.EventType.SELL_CRYPTO) {
                dailyCount.incrementAndGet();
            }
    }

    public long getDailyTransactionCount() {
        return dailyCount.get();
    }

    public void resetDailyCount() {
        dailyCount.set(0);
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
