package com.example.backend.portfolio;

import com.example.backend.CoinGecko.CoinGeckoService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.user.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private CoinGeckoService coinGeckoService;



    public Portfolio createPortfolio(String name) {
        User currentUser = authenticationService.getCurrentUser();

        Optional<Portfolio> existingPortfolio = portfolioRepository.findByUserAndName(currentUser, name);
        if (existingPortfolio.isPresent()) {
            throw new RuntimeException("Portfolio with that name already exist");
        }
        Portfolio portfolio = Portfolio.builder()
                .user(currentUser)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return portfolioRepository.save(portfolio);
    }

    public List<Portfolio> getUserPortfolios() {
        User currentUser = authenticationService.getCurrentUser();
        List<Portfolio> portfolios = portfolioRepository.findByUser(currentUser);
        return portfolios;
    }

    @Transactional
    public Portfolio getUserPortfolioById(Integer portfolioID) {
        User currentUser = authenticationService.getCurrentUser();
        return portfolioRepository.findWithAssetsByPortfolioIDAndUser(portfolioID, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found " + portfolioID + " " + currentUser.getEmail()));
    }
    public List<PortfolioAssetDTO> getPortfolioAssetsWithGains(Integer portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<String> currencies = portfolio.getPortfolioAssets().stream()
                .map(asset -> asset.getCurrency().getCoinGeckoID())
                .collect(Collectors.toList());

        Map<String, Map<String, Object>> ratesMap = coinGeckoService.getExchangeRatesBatch(currencies);

        List<PortfolioAssetDTO> assetDTOs = new ArrayList<>();
        for (PortfolioAsset asset : portfolio.getPortfolioAssets()) {
            String currencyID = asset.getCurrency().getCoinGeckoID().toLowerCase();
            Map<String, Object> currencyRates = ratesMap.get(currencyID);
            Double currentPrice = currencyRates != null && currencyRates.containsKey("usd")
                    ? ((Number) currencyRates.get("usd")).doubleValue()
                    : null;

            if (currentPrice != null) {
                asset.setCurrentPrice(currentPrice);

                PortfolioAssetDTO dto = getPortfolioAssetDTO(asset, currentPrice);
                assetDTOs.add(dto);
            }
        }

        return assetDTOs;
    }

    private static PortfolioAssetDTO getPortfolioAssetDTO(PortfolioAsset asset, Double currentPrice) {
        Double currentValue = asset.getAmount() * currentPrice;
        Double originalValue = asset.getAmount() * asset.getAveragePurchasePrice();
        Double gainOrLoss = currentValue - originalValue;

        PortfolioAssetDTO dto = new PortfolioAssetDTO();
        dto.setCurrencyName(asset.getCurrency().getName());
        dto.setAmount(asset.getAmount());
        dto.setAveragePurchasePrice(asset.getAveragePurchasePrice());
        dto.setCurrentPrice(currentPrice);
        dto.setGainOrLoss(gainOrLoss);
        return dto;
    }
    public Double calculateTotalPortfolioGainOrLoss(Integer portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<String> currencies = portfolio.getPortfolioAssets().stream()
                .map(asset -> asset.getCurrency().getCoinGeckoID())
                .collect(Collectors.toList());

        Map<String, Map<String, Object>> ratesMap = coinGeckoService.getExchangeRatesBatch(currencies);

        double totalInitialValue = 0.0;
        double totalCurrentValue = 0.0;

        for (PortfolioAsset asset : portfolio.getPortfolioAssets()) {
            String currencyID = asset.getCurrency().getCoinGeckoID().toLowerCase();
            Map<String, Object> currencyRates = ratesMap.get(currencyID);
            Double currentPrice = currencyRates != null && currencyRates.containsKey("usd")
                    ? ((Number) currencyRates.get("usd")).doubleValue()
                    : null;

            if (currentPrice != null) {
                Double currentValue = asset.getAmount() * currentPrice;
                Double initialValue = asset.getAmount() * asset.getAveragePurchasePrice();

                totalInitialValue += initialValue;
                totalCurrentValue += currentValue;
            }
        }

        // Zysk/strata całego portfela
        return totalCurrentValue - totalInitialValue;
    }

}
