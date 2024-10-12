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
    public ResponseEntity<List<PortfolioDTO>> getUserPortfolios() {
        List<PortfolioDTO> portfolios = portfolioService.getUserPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getUserPortfolioByid(@PathVariable Integer id) {
        try {
            Portfolio portfolio = portfolioService.getUserPortfolioByid(id);
            return ResponseEntity.ok(portfolio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/{id}/gains")
    public ResponseEntity<List<PortfolioAssetDTO>> getPortfolioAssetsWithGains(@PathVariable Integer id) {
        try {
            List<PortfolioAssetDTO> assetsWithGains = portfolioService.getPortfolioAssetsWithGains(id);
            return ResponseEntity.ok(assetsWithGains);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/{id}/total-gain-or-loss")
    public ResponseEntity<Double> getTotalPortfolioGainOrLoss(@PathVariable Integer id) {
        try {
            Double gainOrLoss = portfolioService.calculateTotalPortfolioGainOrLoss(id);
            return ResponseEntity.ok(gainOrLoss);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

