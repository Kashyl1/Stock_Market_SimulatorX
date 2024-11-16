package com.example.backend.portfolio;

import com.example.backend.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Portfolio Asset Repository", description = "Repository interface for PortfolioAsset entity")
public interface PortfolioAssetRepository extends JpaRepository<PortfolioAsset, Integer> {

    Optional<PortfolioAsset> findByPortfolioAndCurrency(Portfolio portfolio, Currency currency);

    void deleteAllByPortfolio(Portfolio portfolio);
}
