package com.example.backend.portfolio;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    List<Portfolio> findByUser(User user);

    Optional<Portfolio> findByPortfolioIDAndUser(Integer portfolioID, User user);

    Optional<Portfolio> findByUserAndName(User user, String name);
    @EntityGraph(attributePaths = {"portfolioAssets", "portfolioAssets.currency"})
    Optional<Portfolio> findWithAssetsByPortfolioIDAndUser(Integer portfolioID, User user);

}
