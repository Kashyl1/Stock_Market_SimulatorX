package com.example.backend.alert.global;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "global_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a global alert sent to all users")
public class GlobalAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the global alert", example = "1")
    private Integer globalAlertid;

    @Column(nullable = false)
    @Schema(description = "The message content of the global alert", example = "System maintenance scheduled at midnight")
    private String message;

    @Column(nullable = false)
    @Schema(description = "The creation timestamp of the alert")
    private LocalDateTime createdAt;

    @Schema(description = "The scheduled time for the alert to be sent")
    private LocalDateTime scheduledFor;

    @Column(nullable = false)
    @Schema(description = "Indicates whether the alert is active", example = "true")
    private boolean active;
}
