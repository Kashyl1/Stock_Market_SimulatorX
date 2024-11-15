package com.example.backend.admin;

import com.example.backend.portfolio.PortfolioDTO;
import com.example.backend.portfolio.PortfolioService;
import com.example.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/portfolios")
@RequiredArgsConstructor
public class AdminPortfolioController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<PortfolioDTO>> getAllPortfolios(Pageable pageable) {
        Page<PortfolioDTO> portfolios = portfolioService.getAllPortfolios(pageable);
        return ResponseEntity.ok(portfolios);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable Integer portfolioId) {
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PortfolioDTO>> getPortfoliosByUserId(@PathVariable Integer userId, Pageable pageable) {
        Page<PortfolioDTO> portfolios = portfolioService.getPortfoliosByUserId(userId, pageable);
        return ResponseEntity.ok(portfolios);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<String> deletePortfolio(@PathVariable Integer portfolioId) {
        portfolioService.deletePortfolioById(portfolioId);
        return ResponseEntity.ok("Portfolio has been deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(@PathVariable Integer portfolioId, @RequestBody UpdatePortfolioRequest request) {
        PortfolioDTO updatedPortfolio = portfolioService.updatePortfolio(portfolioId, request);
        return ResponseEntity.ok(updatedPortfolio);
    }
}
