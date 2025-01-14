package com.example.backend.portfolio;

import com.example.backend.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Portfolios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "verificationToken"})
@Schema(description = "Represents a user's investment portfolio")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the portfolio", example = "1")
    private Integer portfolioid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    @Schema(description = "User who owns the portfolio")
    private User user;

    @Column(nullable = false, length = 100)
    @Schema(description = "Name of the portfolio", example = "Retirement Fund")
    private String name;

    @Column(nullable = false)
    @Schema(description = "Timestamp when the portfolio was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Schema(description = "Timestamp when the portfolio was last updated", example = "2023-10-05T15:30:00")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "List of assets contained in the portfolio")
    private List<PortfolioAsset> portfolioAssets = new ArrayList<>();

    @Column(nullable = false)
    private boolean deleted = false;
}
