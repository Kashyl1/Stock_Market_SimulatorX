package com.example.backend.portfolio;

import com.example.backend.admin.UpdatePortfolioRequest;
import com.example.backend.alert.trade.TradeAlertRepository;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.PortfolioAlreadyExistsException;
import com.example.backend.exceptions.PortfolioNotFoundException;
import com.example.backend.exceptions.UserNotFoundException;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AuthenticationService authenticationService;
    private final PortfolioMapper portfolioMapper;
    private final UserRepository userRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final TransactionRepository transactionRepository;
    private final TradeAlertRepository tradeAlertRepository;


    @Transactional
    public Portfolio createPortfolio(String name) {
        String email = authenticationService.getCurrentUserEmail();
        User currentUser = authenticationService.getCurrentUser(email);
        Optional<Portfolio> existingPortfolio = portfolioRepository.findByUserAndName(currentUser, name);
        if (existingPortfolio.isPresent()) {
            throw new PortfolioAlreadyExistsException("Portfolio with that name already exists");
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
                    asset.getCurrency().getImageUrl(), // tu
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
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

        List<PortfolioAssetDTO> assets = portfolio.getPortfolioAssets().stream().map(asset -> new PortfolioAssetDTO(
                asset.getCurrency().getName(),
                asset.getCurrency().getImageUrl(), //tu
                asset.getAmount(),
                asset.getAveragePurchasePrice(),
                asset.getCurrentPrice(),
                null,
                asset.getCurrency().getCurrencyid()
        )).collect(Collectors.toList());


        return PortfolioDTO.builder()
                .portfolioid(portfolio.getPortfolioid())
                .name(portfolio.getName())
                .portfolioAssets(assets)
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }

    @Transactional
    public List<PortfolioAssetDTO> getPortfolioAssetsWithGains(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findWithAssetsByPortfolioidAndUser(portfolioid, authenticationService.getCurrentUser(authenticationService.getCurrentUserEmail()))
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

        return portfolio.getPortfolioAssets().stream()
                .map(asset -> {
                    BigDecimal amount = asset.getAmount();

                    BigDecimal currentCurrencyPrice = asset.getCurrency().getCurrentPrice();

                    BigDecimal gainOrLoss = amount.multiply(currentCurrencyPrice.subtract(asset.getAveragePurchasePrice()));

                    return PortfolioAssetDTO.builder()
                            .currencyName(asset.getCurrency().getName())
                            .imageUrl(asset.getCurrency().getImageUrl()) //tutaj
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
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));

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

    @Transactional
    public Page<PortfolioDTO> getAllPortfolios(Pageable pageable) {
        return portfolioRepository.findAll(pageable).map(portfolioMapper::toDTO);
    }

    @Transactional
    public PortfolioDTO getPortfolioById(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findById(portfolioid)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
        return portfolioMapper.toDTO(portfolio);
    }

    @Transactional
    public Page<PortfolioDTO> getPortfoliosByUserId(Integer userid, Pageable pageable) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return portfolioRepository.findByUser(user, pageable).map(portfolioMapper::toDTO);

    }

    @Transactional
    public void deletePortfolioById(Integer portfolioid) {
        Portfolio portfolio = portfolioRepository.findById(portfolioid)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
        transactionRepository.deleteAllByPortfolio(portfolio);
        tradeAlertRepository.deleteAllByPortfolio(portfolio);

        portfolioAssetRepository.deleteAllByPortfolio(portfolio);
        portfolioRepository.delete(portfolio);
    }

    @Transactional
    public PortfolioDTO updatePortfolio(Integer portfolioId, UpdatePortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioNotFoundException("Portfolio not found"));
        if (request.getName() != null && !request.getName().isEmpty()) {
            portfolio.setName(request.getName());
            portfolio.setUpdatedAt(LocalDateTime.now());
            portfolioRepository.save(portfolio);
        }
        return portfolioMapper.toDTO(portfolio);
    }
}
