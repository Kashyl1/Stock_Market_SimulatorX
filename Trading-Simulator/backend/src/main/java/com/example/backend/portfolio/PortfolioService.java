package com.example.backend.portfolio;

import com.example.backend.CoinGecko.CoinGeckoService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.transaction.TransactionService;
import com.example.backend.user.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private CoinGeckoService coinGeckoService;

    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);



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
    public List<PortfolioDTO> getUserPortfolios() {
        User currentUser = authenticationService.getCurrentUser();

        List<Portfolio> portfolios = portfolioRepository.findByUser(currentUser);

        return portfolios.stream().map(portfolio -> {
            List<PortfolioAssetDTO> assets = portfolio.getPortfolioAssets().stream().map(asset -> new PortfolioAssetDTO(asset.getCurrency().getName(), asset.getAmount(), asset.getAveragePurchasePrice(), asset.getCurrentPrice(), null)).collect(Collectors.toList());
            return new PortfolioDTO(portfolio.getPortfolioid(), portfolio.getName(), assets);
        }).collect(Collectors.toList());
    }
    @Transactional
    public Portfolio getUserPortfolioByid(Integer portfolioid) {
        User currentUser = authenticationService.getCurrentUser();
        return portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found " + portfolioid + " " + currentUser.getEmail()));
    }
    public List<PortfolioAssetDTO> getPortfolioAssetsWithGains(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findById(portfolioid)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<String> currencies = portfolio.getPortfolioAssets().stream()
                .map(asset -> asset.getCurrency().getCoinGeckoid())
                .collect(Collectors.toList());

        Map<String, Map<String, Object>> ratesMap = coinGeckoService.getExchangeRatesBatch(currencies);

        List<PortfolioAssetDTO> assetDTOs = portfolio.getPortfolioAssets().parallelStream()
                .map(asset -> {
                    String currencyid = asset.getCurrency().getCoinGeckoid().toLowerCase();
                    Map<String, Object> currencyRates = ratesMap.get(currencyid);
                    Double currentPrice = currencyRates != null && currencyRates.containsKey("usd")
                            ? ((Number) currencyRates.get("usd")).doubleValue()
                            : null;

                    if (currentPrice != null) {
                        asset.setCurrentPrice(currentPrice);
                        return getPortfolioAssetDTO(asset, currentPrice);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


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
    public Double calculateTotalPortfolioGainOrLoss(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findById(portfolioid)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<String> currencies = portfolio.getPortfolioAssets().stream()
                .map(asset -> asset.getCurrency().getCoinGeckoid())
                .collect(Collectors.toList());

        Map<String, Map<String, Object>> ratesMap = coinGeckoService.getExchangeRatesBatch(currencies);

        double totalInitialValue = 0.0;
        double totalCurrentValue = 0.0;

        for (PortfolioAsset asset : portfolio.getPortfolioAssets()) {
            String currencyid = asset.getCurrency().getCoinGeckoid().toLowerCase();
            Map<String, Object> currencyRates = ratesMap.get(currencyid);
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

        return totalCurrentValue - totalInitialValue;
    }

}
