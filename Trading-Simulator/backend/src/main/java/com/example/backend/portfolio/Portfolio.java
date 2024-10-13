package com.example.backend.portfolio;

import com.example.backend.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

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
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer portfolioid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PortfolioAsset> portfolioAssets = new ArrayList<>();
}
