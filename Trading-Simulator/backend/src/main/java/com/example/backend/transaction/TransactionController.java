package com.example.backend.transaction;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/buy-asset")
    public ResponseEntity<String> buyAsset(@Valid @RequestBody BuyAssetRequest request, Authentication authentication) {
        User currentUser = authenticationService.getCurrentUser(authentication.getName());

        if ((request.getAmountInUSD() == null && request.getAmountOfCurrency() == null) ||
                (request.getAmountInUSD() != null && request.getAmountOfCurrency() != null)) {
            return ResponseEntity.badRequest().body("Please provide amount in USD or amount Of Currency not both");
        }

        transactionService.buyAsset(request.getPortfolioid(), request.getCurrencyid(), request.getAmountInUSD(), request.getAmountOfCurrency(), currentUser
        );
        return ResponseEntity.ok("Asset purchased successfully");
    }

    @PostMapping("/sell-asset")
    public ResponseEntity<String> sellAsset(@Valid @RequestBody SellAssetRequest request, Authentication authentication) {
        User currentUser = authenticationService.getCurrentUser(authentication.getName());

        if ((request.getAmount() == null && request.getPriceInUSD() == null) ||
                (request.getAmount() != null && request.getPriceInUSD() != null)) {
            return ResponseEntity.badRequest().body("Please provide amount in USD or amount Of Currency not both.");
        }

        transactionService.sellAsset(request.getPortfolioid(), request.getCurrencyid(), request.getAmount(), request.getPriceInUSD(), currentUser
        );
        return ResponseEntity.ok("Asset sold successfully");
    }

    @GetMapping("/available-assets")
    public ResponseEntity<Page<Map<String, Object>>> getAvailableAssets(
            @PageableDefault(page = 0, size = 50, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
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
