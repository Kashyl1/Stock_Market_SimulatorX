package com.example.backend.adminEvent;

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
public class AdminEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "admin_email")
    private String adminEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType eventType;

    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public enum EventType {
        CREATE_GLOBAL_ALERT,
        DELETE_GLOBAL_ALERT,
        CREATE_PORTFOLIO,
        DELETE_PORTFOLIO_BY_ID,
        UPDATE_PORTFOLIO,
        GET_ALL_PORTFOLIOS,
        GET_PORTFOLIOS_BY_ID,
        GET_PORTFOLIOS_BY_USER_ID,
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
