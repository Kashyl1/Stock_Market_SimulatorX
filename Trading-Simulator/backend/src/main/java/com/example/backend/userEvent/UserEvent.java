package com.example.backend.userEvent;

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
@Schema(description = "Represents an event triggered by a user")
public class UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Unique identifier of the user event", example = "1")
    private Long id;

    @Column(name = "email")
    @Schema(description = "Email of the user who triggered the event", example = "user@example.com")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    @Schema(description = "Type of the user event", example = "LOGIN")
    private EventType eventType;

    @Column(name = "event_time")
    @Schema(description = "Timestamp when the event occurred", example = "2023-12-01T10:15:30")
    private LocalDateTime eventTime;

    @Column(name = "details", columnDefinition = "TEXT")
    @Schema(description = "Detailed information about the event", example = "createdUsrName: user@example.com createdUserId: 32221 etc etc")
    private String details;

    @Schema(description = "Enumeration of all possible user event types")
    public enum EventType {
        LOGIN,
        REGISTRATION,
        DEPOSIT_FUNDS,
        BUY_CRYPTO,
        SELL_CRYPTO,
        CREATE_PORTFOLIO,
        DELETE_PORTFOLIO,
        DELETE_NOTIFICATION,
        CHANGE_PASSWORD,
        CHANGE_EMAIL,
        CREATE_EMAIL_ALERT,
        CREATE_TRADE_ALERT,
        SEND_VERIFICATION_EMAIL,
        ACCOUNT_VERIFIED,
        RESEND_VERIFICATION_EMAIL,
        PASSWORD_RESET_REQUESTED,
        PASSWORD_RESET_EMAIL_SENT
        }
}
