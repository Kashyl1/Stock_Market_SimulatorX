package com.example.backend.systemlog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SystemLogs")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logID;

    @Column(nullable = false, length = 50)
    private String logType;

    @Column(columnDefinition = "text", nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
