package com.example.backend.admin;


import com.example.backend.adminEvent.AdminEvent;
import com.example.backend.adminEvent.AdminEventService;
import com.example.backend.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/admin/event/admin")
@AllArgsConstructor
@Tag(name = "Admin Event Controller", description = "Management of admin events")
public class AdminEventController {

    private final AdminEventService adminEventService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all admin events", description = "Retrieve a paginated list of all admin events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin events retrieved successfully")
    })
    public ResponseEntity<Page<AdminEvent>> getAllEvents(Pageable pageable) {
        Page<AdminEvent> adminEvents = adminEventService.getAllEvents(pageable);
        return ResponseEntity.ok(adminEvents);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get an admin event by ID", description = "Retrieve details of a specific admin event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin event retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Admin event not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AdminEvent> getEventById(
            @Parameter(description = "ID of the admin event to retrieve") @PathVariable Long id) {
        AdminEvent event = adminEventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    @Operation(summary = "Get admin event statistics", description = "Retrieve statistics of admin events grouped by event type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin event statistics retrieved successfully")
    })
    public ResponseEntity<Map<String, Long>> getEventStatistics() {
        Map<String, Long> events = adminEventService.getEventStatistics();
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/latest")
    @Operation(summary = "Get the latest admin events", description = "Retrieve the latest admin events, with a configurable limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest admin events retrieved successfully")
    })
    public ResponseEntity<List<AdminEvent>> getLatestEvents(
            @Parameter(description = "Limit for the number of events to retrieve") @RequestParam(defaultValue = "10") int limit) {
        List<AdminEvent> events = adminEventService.getLatestEvents(limit);
        return ResponseEntity.ok(events);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an admin event", description = "Delete a specific admin event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Admin event not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteEventById(
            @Parameter(description = "ID of the admin event to delete") @PathVariable long id) {
        adminEventService.deleteEventById(id);
        return ResponseEntity.ok("Event has been deleted!");
    }
}
