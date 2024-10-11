package com.example.backend.portfolio;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.user.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AuthenticationService authenticationService;

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
}
