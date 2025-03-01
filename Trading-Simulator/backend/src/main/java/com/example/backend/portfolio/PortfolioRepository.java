package com.example.backend.portfolio;

import com.example.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Portfolio Repository", description = "Repository interface for Portfolio entity")
public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    @EntityGraph(attributePaths = {"portfolioAssets.currency"})
    List<Portfolio> findByUser(User user);

    Page<Portfolio> findByUser(User user, Pageable pageable);
    Optional<Portfolio> findByPortfolioidAndUser(Integer portfolioid, User user);
    List<Portfolio> findByUserAndDeletedFalse(User user);
    Optional<Portfolio> findByUserAndName(User user, String name);
    Page<Portfolio> findAllByDeletedFalse(Pageable pageable);
    @Query("""
    SELECT p
    FROM Portfolio p
    WHERE p.user = :user
      AND p.name = :name
      AND p.deleted = false
    """)
    Optional<Portfolio> findActiveByUserAndName(@Param("user") User user, @Param("name") String name);

    @EntityGraph(attributePaths = {"portfolioAssets.currency", "portfolioAssets"})
    @Query("""
    SELECT p 
    FROM Portfolio p 
    WHERE p.portfolioid = :portfolioid 
      AND p.user = :user 
      AND p.deleted = false
    """)
    Optional<Portfolio> findWithAssetsByPortfolioidAndUserAndDeletedFalse(
            @Param("portfolioid") Integer portfolioid,
            @Param("user") User user
    );

    @Query("""
    SELECT COALESCE(SUM(pa.amount * (pa.currency.currentPrice - pa.averagePurchasePrice)), 0)
    FROM Portfolio p
         JOIN p.portfolioAssets pa
    WHERE p.user.id = :userId
      AND p.deleted = false
""")
    BigDecimal findGlobalGainLossByUserId(@Param("userId") Integer userId);

    @Query("""
    SELECT p.user.id AS userId,
       p.user.firstname AS firstname,
       COALESCE(SUM(pa.amount * (pa.currency.currentPrice - pa.averagePurchasePrice)), 0) AS totalGain
    FROM Portfolio p
    JOIN p.portfolioAssets pa
    WHERE p.deleted = false
    GROUP BY p.user.id, p.user.firstname
    ORDER BY totalGain DESC
    """)
    List<UserRankingProjection> findUserRanking(Pageable pageable);


}
