package com.example.backend.portfolio;

import com.example.backend.CoinGecko.CoinGeckoService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.user.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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


    @Transactional
    @CacheEvict(value = "userPortfolios", key = "@authenticationService.getCurrentUserEmail()")
    public Portfolio createPortfolio(String name) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
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

    @Transactional
    @Cacheable(value = "userPortfolios", key = "@authenticationService.getCurrentUserEmail()")
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
                    asset.getCurrency().getCoinGeckoid()
            )).collect(Collectors.toList());
            return new PortfolioDTO(portfolio.getPortfolioid(), portfolio.getName(), assets, portfolio.getCreatedAt(), portfolio.getUpdatedAt());
        }).collect(Collectors.toList());
    }
    @Transactional
    @Cacheable(value = "portfolioById", key = "#portfolioid")
    public PortfolioDTO getUserPortfolioByid(Integer portfolioid) {
        User currentUser = authenticationService.getCurrentUser(authenticationService.getCurrentUserEmail());
        Portfolio portfolio = portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, currentUser)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        logger.info("Mapping portfolio to PortfolioDTO: {}", portfolio);

        List<PortfolioAssetDTO> assets = portfolio.getPortfolioAssets().stream().map(asset -> new PortfolioAssetDTO(
                asset.getCurrency().getName(),
                asset.getAmount(),
                asset.getAveragePurchasePrice(),
                asset.getCurrentPrice(),
                null,
                asset.getCurrency().getCoinGeckoid()
        )).collect(Collectors.toList());

        PortfolioDTO portfolioDTO = PortfolioDTO.builder()
                .portfolioid(portfolio.getPortfolioid())
                .name(portfolio.getName())
                .portfolioAssets(assets)
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();

        logger.info("Successfully mapped to PortfolioDTO: {}", portfolioDTO);

        return portfolioDTO;
    }
    @Transactional
    @CacheEvict(value = "portfolioAssetsWithGains", key = "#portfolioid")
    public List<PortfolioAssetDTO> getPortfolioAssetsWithGains(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, authenticationService.getCurrentUser(authenticationService.getCurrentUserEmail()))
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        List<String> currencies = portfolio.getPortfolioAssets().stream()
                .map(asset -> asset.getCurrency().getCoinGeckoid())
                .collect(Collectors.toList());

        Map<String, Map<String, Object>> ratesMap = coinGeckoService.getExchangeRatesBatch(currencies);

        return portfolio.getPortfolioAssets().parallelStream()
                .map(asset -> {
                    String currencyid = asset.getCurrency().getCoinGeckoid().toLowerCase();
                    Map<String, Object> currencyRates = ratesMap.get(currencyid);
                    Double currentPrice = currencyRates != null && currencyRates.containsKey("usd")
                            ? ((Number) currencyRates.get("usd")).doubleValue()
                            : null;
                    if (currentPrice != null) {
                        asset.setCurrentPrice(currentPrice);
                        return PortfolioAssetDTO.builder()
                                .currencyName(asset.getCurrency().getName())
                                .amount(asset.getAmount())
                                .averagePurchasePrice(asset.getAveragePurchasePrice())
                                .currentPrice(asset.getCurrentPrice())
                                .gainOrLoss(asset.getAmount() * asset.getCurrentPrice() - asset.getAmount() * asset.getAveragePurchasePrice())
                                .coinGeckoid(asset.getCurrency().getCoinGeckoid())
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    @Transactional
    @Cacheable(value = "portfolioGainLoss", key = "#portfolioid")
    public Double calculateTotalPortfolioGainOrLoss(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, authenticationService.getCurrentUser(authenticationService.getCurrentUserEmail()))
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
