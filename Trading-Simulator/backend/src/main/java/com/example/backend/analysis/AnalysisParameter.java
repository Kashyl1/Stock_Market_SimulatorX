package com.example.backend.analysis;

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
@Table(name = "AnalysisParameters")
public class AnalysisParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer parameterID;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String parameterName;

    @Column(columnDefinition = "text", nullable = false)
    private String parameters;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
