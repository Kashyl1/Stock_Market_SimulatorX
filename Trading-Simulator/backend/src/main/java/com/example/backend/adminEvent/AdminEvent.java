package com.example.backend.adminEvent;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents an event triggered by an admin")
public class AdminEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Unique identifier of the admin event", example = "1")
    private long id;

    @Column(name = "admin_email")
    @Schema(description = "Email of the admin who triggered the event", example = "admin@example.com")
    private String adminEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    @Schema(description = "Type of the admin event", example = "CREATE_GLOBAL_ALERT")
    private EventType eventType;

    @Column(name = "event_time")
    @Schema(description = "Timestamp when the event occurred", example = "2023-12-01T15:30:00")
    private LocalDateTime eventTime;

    @Column(name = "details", columnDefinition = "TEXT")
    @Schema(description = "Detailed information about the event", example = "createdAdminName: admin@example.com createdAdminId: 32221 etc etc")
    private String details;

    @Schema(description = "Enumeration of all possible admin event types")
    public enum EventType {
        CREATE_GLOBAL_ALERT,
        DELETE_GLOBAL_ALERT,
        DELETE_PORTFOLIO_BY_ID,
        UPDATE_PORTFOLIO,
        GET_ALL_PORTFOLIOS,
        GET_ALL_USERS,
        GET_USER_BY_ID,
        UPDATE_USER,
        BLOCK_USER,
        UNBLOCK_USER,
        DELETE_USER,
        MARK_TRANSACTION_SUSPICIOUS,
        GET_ALL_TRANSACTIONS,
        GET_TRANSACTIONS_BY_USER,
        CREATE_ADMIN
    }
}
