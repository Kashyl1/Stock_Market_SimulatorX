package com.example.backend.transaction;

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
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthenticationService authenticationService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/buy-asset")
    public ResponseEntity<String> buyAsset(@Valid @RequestBody BuyAssetRequest request) {
        transactionService.buyAsset(request.getPortfolioid(), request.getCurrencyid(), request.getAmountInUSD());
        return ResponseEntity.ok("Asset purchased successfully");
    }

    @PostMapping("/sell-asset")
    public ResponseEntity<String> sellAsset(@Valid @RequestBody SellAssetRequest request) {
        transactionService.sellAsset(request.getPortfolioid(), request.getCurrencyid(), request.getAmount());
        return ResponseEntity.ok("Asset sold successfully");
    }

    @GetMapping("/available-assets")
    public ResponseEntity<Page<Map<String, Object>>> getAvailableAssets(
            @PageableDefault(page = 0, size = 50, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        logger.info("Received request for available assets: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Map<String, Object>> assets = transactionService.getAvailableAssetsWithPrices(pageable);
        return ResponseEntity.ok(assets);
    }

    // przypominajka
    @GetMapping("/history")
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionHistory(
            @PageableDefault(page = 0, size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionHistory(pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/history/portfolio/{portfolioid}")
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionHistoryByPortfolio(
            @PathVariable Integer portfolioid,
            @PageableDefault(page = 0, size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionHistoryByPortfolio(portfolioid, pageable);
        return ResponseEntity.ok(transactions);
    }
}
