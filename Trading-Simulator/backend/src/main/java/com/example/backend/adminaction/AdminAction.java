package com.example.backend.adminaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AdminActions")
public class AdminAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer actionID;

    @ManyToOne
    @JoinColumn(name = "adminID", nullable = false)
    private User admin;

    @Column(nullable = false, length = 100)
    private String actionType;

    @Column(columnDefinition = "text", nullable = false)
    private String details;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
