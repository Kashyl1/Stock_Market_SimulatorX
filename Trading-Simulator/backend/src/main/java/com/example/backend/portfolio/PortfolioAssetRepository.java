package com.example.backend.portfolio;

import com.example.backend.currency.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioAssetRepository extends JpaRepository<PortfolioAsset, Integer> {
    Optional<PortfolioAsset> findByPortfolioAndCurrency(Portfolio portfolio, Currency currency);
}
