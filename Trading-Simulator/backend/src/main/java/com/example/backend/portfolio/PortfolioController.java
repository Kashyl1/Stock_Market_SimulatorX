package com.example.backend.portfolio;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;
    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    @PostMapping("/create")
    public ResponseEntity<PortfolioDTO> createPortfolio(@Valid @RequestBody CreatePortfolioRequest request) {
        Portfolio portfolio = portfolioService.createPortfolio(request.getName());
        PortfolioDTO portfolioDTO = new PortfolioDTO(portfolio.getPortfolioid(), portfolio.getName(), null, portfolio.getCreatedAt(), portfolio.getUpdatedAt());
        return ResponseEntity.ok(portfolioDTO);
    }

    @GetMapping("/my-portfolios")
    public ResponseEntity<List<PortfolioDTO>> getUserPortfolios() {
        List<PortfolioDTO> portfolios = portfolioService.getUserPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getUserPortfolioByid(@PathVariable Integer id) {
        PortfolioDTO portfolioDTO = portfolioService.getUserPortfolioByid(id);
        return ResponseEntity.ok(portfolioDTO);
    }

    @GetMapping("/{id}/gains")
    public ResponseEntity<List<PortfolioAssetDTO>> getPortfolioAssetsWithGains(@PathVariable Integer id) {
            List<PortfolioAssetDTO> assetsWithGains = portfolioService.getPortfolioAssetsWithGains(id);
            return ResponseEntity.ok(assetsWithGains);
    }

    @GetMapping("/{id}/total-gain-or-loss")
    public ResponseEntity<BigDecimal> getTotalPortfolioGainOrLoss(@PathVariable Integer id) {
            BigDecimal gainOrLoss = portfolioService.calculateTotalPortfolioGainOrLoss(id);
            return ResponseEntity.ok(gainOrLoss);
    }
}
