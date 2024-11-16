package com.example.backend.portfolio;

import com.example.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Portfolio Repository", description = "Repository interface for Portfolio entity")
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    @EntityGraph(attributePaths = {"portfolioAssets.currency"})
    List<Portfolio> findByUser(User user);

    Page<Portfolio> findByUser(User user, Pageable pageable);

    void deleteAllByUser(User user);

    Optional<Portfolio> findByPortfolioidAndUser(Integer portfolioid, User user);

    Optional<Portfolio> findByUserAndName(User user, String name);

    @EntityGraph(attributePaths = {"portfolioAssets.currency", "portfolioAssets"})
    @Query("SELECT p FROM Portfolio p WHERE p.portfolioid = :portfolioid AND p.user = :user")
    Optional<Portfolio> findWithAssetsByPortfolioidAndUser(@Param("portfolioid") Integer portfolioid, @Param("user") User user);
}
