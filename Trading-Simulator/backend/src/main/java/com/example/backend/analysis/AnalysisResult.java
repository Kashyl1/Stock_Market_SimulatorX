package com.example.backend.analysis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.user.User;
import com.example.backend.currency.Currency;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "AnalysisResults")
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer analysisID;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String analysisType;

    @ManyToOne
    @JoinColumn(name = "currencyID", nullable = false)
    private Currency currency;

    @Column(columnDefinition = "text", nullable = false)
    private String parameters;

    @Column(columnDefinition = "text", nullable = false)
    private String result;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
