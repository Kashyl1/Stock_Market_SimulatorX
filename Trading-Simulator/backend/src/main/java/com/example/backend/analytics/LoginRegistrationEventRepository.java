package com.example.backend.analytics;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginRegistrationEventRepository extends JpaRepository<LoginRegistrationEvent, Long> {

    List<LoginRegistrationEvent> findByEventTypeAndEventTimeBetween(LoginRegistrationEvent.EventType eventType, LocalDateTime start, LocalDateTime end);
}
