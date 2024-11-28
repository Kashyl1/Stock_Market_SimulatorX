package com.example.backend.userEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserEventRepository extends JpaRepository<UserEvent, Long> {

    List<UserEvent> findByEventTypeAndEventTimeBetween(UserEvent.EventType eventType, LocalDateTime start, LocalDateTime end);
}
