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
    private Long id;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @Column(name = "details", columnDefinition = "TEXT")
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
