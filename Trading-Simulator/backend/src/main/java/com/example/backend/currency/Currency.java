package com.example.backend.currency;

import com.example.backend.portfolio.PortfolioAsset;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer currencyid;

    @Column(nullable = false, length = 10, unique = true)
    private String symbol;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(nullable = false, unique = true)
    private String coinGeckoid;

    @OneToMany(mappedBy = "currency", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @JsonIgnore
    private List<PortfolioAsset> portfolioAssets;
}
