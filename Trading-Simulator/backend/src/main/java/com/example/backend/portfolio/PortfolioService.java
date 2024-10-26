package com.example.backend.portfolio;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.currency.CurrencyRepository;
import com.example.backend.user.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional
    public Portfolio createPortfolio(String name) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Optional<Portfolio> existingPortfolio = portfolioRepository.findByUserAndName(currentUser, name);
        if (existingPortfolio.isPresent()) {
            throw new RuntimeException("Portfolio with that name already exists");
        }
        Portfolio portfolio = Portfolio.builder()
                .user(currentUser)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return portfolioRepository.save(portfolio);
    }

    @Transactional
    public List<PortfolioDTO> getUserPortfolios() {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);

        List<Portfolio> portfolios = portfolioRepository.findByUser(currentUser);

        return portfolios.stream().map(portfolio -> {
            List<PortfolioAssetDTO> assets = portfolio.getPortfolioAssets().stream().map(asset -> new PortfolioAssetDTO(
                    asset.getCurrency().getName(),
                    asset.getAmount(),
                    asset.getAveragePurchasePrice(),
                    asset.getCurrentPrice(),
                    null,
                    asset.getCurrency().getCurrencyid()
            )).collect(Collectors.toList());
            return new PortfolioDTO(portfolio.getPortfolioid(), portfolio.getName(), assets, portfolio.getCreatedAt(), portfolio.getUpdatedAt());
        }).collect(Collectors.toList());
    }

    @Transactional
    public PortfolioDTO getUserPortfolioByid(Integer portfolioid) {
        User currentUser = authenticationService.getCurrentUser(authenticationService.getCurrentUserEmail());
        Portfolio portfolio = portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<PortfolioAssetDTO> assets = portfolio.getPortfolioAssets().stream().map(asset -> new PortfolioAssetDTO(
                asset.getCurrency().getName(),
                asset.getAmount(),
                asset.getAveragePurchasePrice(),
                asset.getCurrentPrice(),
                null,
                asset.getCurrency().getCurrencyid()
        )).collect(Collectors.toList());

        PortfolioDTO portfolioDTO = PortfolioDTO.builder()
                .portfolioid(portfolio.getPortfolioid())
                .name(portfolio.getName())
                .portfolioAssets(assets)
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();


        return portfolioDTO;
    }

    @Transactional
    public List<PortfolioAssetDTO> getPortfolioAssetsWithGains(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, authenticationService.getCurrentUser(authenticationService.getCurrentUserEmail()))
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        return portfolio.getPortfolioAssets().stream()
                .map(asset -> {
                    BigDecimal amount = asset.getAmount();

                    BigDecimal currentCurrencyPrice = asset.getCurrency().getCurrentPrice();

                    BigDecimal gainOrLoss = amount.multiply(currentCurrencyPrice.subtract(asset.getAveragePurchasePrice()));

                    return PortfolioAssetDTO.builder()
                            .currencyName(asset.getCurrency().getName())
                            .amount(amount)
                            .averagePurchasePrice(asset.getAveragePurchasePrice())
                            .currentPrice(currentCurrencyPrice)
                            .gainOrLoss(gainOrLoss)
                            .currencyid(asset.getCurrency().getCurrencyid())
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public BigDecimal calculateTotalPortfolioGainOrLoss(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, authenticationService.getCurrentUser(authenticationService.getCurrentUserEmail()))
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        BigDecimal totalInitialValue = BigDecimal.ZERO;
        BigDecimal totalCurrentValue = BigDecimal.ZERO;

        for (PortfolioAsset asset : portfolio.getPortfolioAssets()) {
            BigDecimal amount = asset.getAmount();
            BigDecimal currentPrice = asset.getCurrentPrice() != null ? asset.getCurrentPrice() : BigDecimal.ZERO;
            BigDecimal initialValue = amount.multiply(asset.getAveragePurchasePrice());
            BigDecimal currentValue = amount.multiply(currentPrice);

            totalInitialValue = totalInitialValue.add(initialValue);
            totalCurrentValue = totalCurrentValue.add(currentValue);
        }

        return totalCurrentValue.subtract(totalInitialValue);
    }
}
