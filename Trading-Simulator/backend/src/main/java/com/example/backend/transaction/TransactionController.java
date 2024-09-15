package com.example.backend.transaction;
import com.example.backend.CoinGecko.CoinGeckoService;
import com.example.backend.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.user.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private CoinGeckoService coinGeckoService;
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Endpoint do kupowania kryptowalut
     */
   /* @PostMapping("/buy-crypto")
    public ResponseEntity<BuyCryptoResponse> buyCrypto(@RequestBody BuyCryptoRequest request) {
        BuyCryptoResponse response = transactionService.buyCrypto(request.getSymbol(), request.getAmountInUsd());
        return ResponseEntity.ok(response);
    } */
    /**
     * Endpoint do pobierania dostępnych aktywów
     */
    @GetMapping("/available-assets")
    public ResponseEntity<List<Map<String, Object>>> getAvailableAssets() {
        User currentUser = authenticationService.getCurrentUser();
        List<Map<String, Object>> assets = transactionService.getAvailableAssetsWithPrices();
        return ResponseEntity.ok(assets);
    }
}
