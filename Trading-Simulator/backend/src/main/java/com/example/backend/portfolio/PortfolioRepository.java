package com.example.backend.portfolio;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {
    @EntityGraph(attributePaths = {"portfolioAssets.currency"})
    List<Portfolio> findByUser(User user);
    void deleteAllByUser(User user);

    Optional<Portfolio> findByPortfolioidAndUser(Integer portfolioid, User user);

    Optional<Portfolio> findByUserAndName(User user, String name);

    @EntityGraph(attributePaths = {"portfolioAssets.currency", "portfolioAssets"})
    @Query("SELECT p FROM Portfolio p WHERE p.portfolioid = :portfolioid AND p.user = :user")
    Optional<Portfolio> findWithAssetsByPortfolioidAndUser(@Param("portfolioid") Integer portfolioid, @Param("user") User user);

}
