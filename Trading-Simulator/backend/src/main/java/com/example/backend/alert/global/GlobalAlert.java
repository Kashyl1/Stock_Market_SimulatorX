package com.example.backend.alert.global;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "global_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer globalAlertid;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime scheduledFor;

    @Column(nullable = false)
    private boolean active;
}
