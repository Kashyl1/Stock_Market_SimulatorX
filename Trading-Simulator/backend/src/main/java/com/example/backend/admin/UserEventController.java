package com.example.backend.admin;

import com.example.backend.exceptions.ErrorResponse;
import com.example.backend.userEvent.UserEvent;
import com.example.backend.userEvent.UserEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin/event/user")
@RequiredArgsConstructor
@Tag(name = "User Event Controller", description = "Management of user events")
public class UserEventController {

    private final UserEventService userEventService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all user events", description = "Retrieve a paginated list of all user events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User events retrieved successfully")
    })
    public ResponseEntity<Page<UserEvent>> getAllEvents(Pageable pageable) {
        Page<UserEvent> userEvents = userEventService.getAllEvents(pageable);
        return ResponseEntity.ok(userEvents);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get a user event by ID", description = "Retrieve details of a specific user event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User event retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User event not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserEvent> getEventById(
            @Parameter(description = "ID of the user event to retrieve") @PathVariable Long id) {
        UserEvent event = userEventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    @Operation(summary = "Get user event statistics", description = "Retrieve statistics of user events grouped by event type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User event statistics retrieved successfully")
    })
    public ResponseEntity<Map<String, Long>> getEventStatistics() {
        Map<String, Long> events = userEventService.getEventStatistics();
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/latest")
    @Operation(summary = "Get the latest user events", description = "Retrieve the latest user events, with a configurable limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest user events retrieved successfully")
    })
    public ResponseEntity<List<UserEvent>> getLatestEvents(
            @Parameter(description = "Limit for the number of events to retrieve") @RequestParam(defaultValue = "10") int limit) {
        List<UserEvent> events = userEventService.getLatestEvents(limit);
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user event", description = "Delete a specific user event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User event not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteEventById(
            @Parameter(description = "ID of the user event to delete") @PathVariable long id) {
        userEventService.deleteEventById(id);
        return ResponseEntity.ok("Event has been deleted!");
    }
}
