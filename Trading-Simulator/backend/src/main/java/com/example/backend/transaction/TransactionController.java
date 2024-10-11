package com.example.backend.transaction;

import com.example.backend.CoinGecko.CoinGeckoService;
import com.example.backend.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CoinGeckoService coinGeckoService;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/buy-asset")
    public ResponseEntity<String> buyAsset(@Valid @RequestBody BuyAssetRequest request) {
        transactionService.buyAsset(request.getPortfolioID(), request.getCurrencyID(), request.getAmountInUSD());
        return ResponseEntity.ok("Asset purchased successfully");
    }

    @PostMapping("/sell-asset")
    public ResponseEntity<String> sellAsset(@Valid @RequestBody SellAssetRequest request) {
        transactionService.sellAsset(request.getPortfolioID(), request.getCurrencyID(), request.getAmount());
        return ResponseEntity.ok("Asset sold successfully");
    }

    @GetMapping("/available-assets")
    public ResponseEntity<List<Map<String, Object>>> getAvailableAssets() {
        List<Map<String, Object>> assets = transactionService.getAvailableAssetsWithPrices();
        return ResponseEntity.ok(assets);
    }
    @GetMapping("/history")
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionHistory(
            @PageableDefault(page = 0, size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionHistory(pageable);
        return ResponseEntity.ok(transactions);
    }
    @GetMapping("/history/portfolio/{portfolioId}")
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionHistoryByPortfolio(
            @PathVariable Integer portfolioId,
            @PageableDefault(page = 0, size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionHistoryByPortfolio(portfolioId, pageable);
        return ResponseEntity.ok(transactions);
    }
}
