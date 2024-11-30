package com.example.backend.userEvent;

import com.example.backend.adminEvent.AdminEvent;
import com.example.backend.exceptions.EventNotFoundException;
import lombok.AllArgsConstructor;
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
public class UserEventService {

    private final UserEventRepository userEventRepository;

    public UserEvent getEventById(Long id) {
        return userEventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with id: " + id + " not found"));
    }

    public Page<UserEvent> getAllEvents(Pageable pageable) {
        return userEventRepository.findAll(pageable);
    }

    public Map<String, Long> getEventStatistics() {
        List<UserEvent> events = userEventRepository.findAll();
        return events.stream()
                .collect(Collectors.groupingBy(
                        event -> event.getEventType().name(),
                        Collectors.counting()
                ));
    }

    public List<UserEvent> getLatestEvents(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("eventTime").descending());
        return userEventRepository.findAll(pageable).getContent();
    }

    public void deleteEventById(long id) {
        UserEvent userEvent = userEventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with id: " + id + " not found"));
        userEventRepository.delete(userEvent);
    }
}
