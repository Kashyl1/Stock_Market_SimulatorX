package com.example.backend.portfolio;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @PostMapping("/create")
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody CreatePortfolioRequest request) {
        Portfolio portfolio = portfolioService.createPortfolio(request.getName());
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/my-portfolios")
    public ResponseEntity<List<Portfolio>> getUserPortfolios() {
        List<Portfolio> portfolios = portfolioService.getUserPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getUserPortfolioById(@PathVariable Integer id) {
        try {
            Portfolio portfolio = portfolioService.getUserPortfolioById(id);
            return ResponseEntity.ok(portfolio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

