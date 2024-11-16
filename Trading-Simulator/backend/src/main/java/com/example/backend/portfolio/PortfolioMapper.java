package com.example.backend.portfolio;

import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.stream.Collectors;

@Component
@Tag(name = "Portfolio Mapper", description = "Maps Portfolio entities to DTOs")
public class PortfolioMapper {

    public PortfolioDTO toDTO(Portfolio portfolio) {
        return PortfolioDTO.builder()
                .portfolioid(portfolio.getPortfolioid())
                .name(portfolio.getName())
                .portfolioAssets(portfolio.getPortfolioAssets().stream().map(asset -> PortfolioAssetDTO.builder()
                        .currencyName(asset.getCurrency().getName())
                        .imageUrl(asset.getCurrency().getImageUrl())
                        .amount(asset.getAmount())
                        .averagePurchasePrice(asset.getAveragePurchasePrice())
                        .currentPrice(asset.getCurrentPrice())
                        .currencyid(asset.getCurrency().getCurrencyid())
                        .build()).collect(Collectors.toList()))
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }
}
