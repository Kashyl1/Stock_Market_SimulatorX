package com.example.backend.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class EventTrackingService {

    private final LoginRegistrationEventRepository eventRepository;

    public void logEvent(String email, LoginRegistrationEvent.EventType eventType) {
        LoginRegistrationEvent event = LoginRegistrationEvent.builder()
                .email(email)
                .eventType(eventType)
                .eventTime(LocalDateTime.now())
                .build();
        eventRepository.save(event);
    }

    public long countEventsByTypeAndDateRange(LoginRegistrationEvent.EventType eventType, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByEventTypeAndEventTimeBetween(eventType, start, end).size();
    }
}
