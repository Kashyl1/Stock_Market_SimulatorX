package com.example.backend.adminEvent;

import com.example.backend.exceptions.EventNotFoundException;
import com.example.backend.userEvent.UserEvent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminEventService {

    private final AdminEventRepository adminEventRepository;

    public AdminEvent getEventById(Long id) {
        return adminEventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with id: " + id + " not found"));
    }

    public Page<AdminEvent> getAllEvents(Pageable pageable) {
        return adminEventRepository.findAll(pageable);
    }

    public Map<String, Long> getEventStatistics() {
        List<AdminEvent> events = adminEventRepository.findAll();
        return events.stream()
                .collect(Collectors.groupingBy(
                        event -> event.getEventType().name(),
                        Collectors.counting()
                ));
    }

    public List<AdminEvent> getLatestEvents(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("eventTime").descending());
        return adminEventRepository.findAll(pageable).getContent();
    }

    public void deleteEventById(long id) {
        AdminEvent adminEvent = adminEventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with id: " + id + " not found"));
        adminEventRepository.delete(adminEvent);
    }
}
