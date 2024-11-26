package com.example.backend.transaction;

import com.example.backend.exceptions.ErrorResponse;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Controller", description = "Handles buying and selling of assets")
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthenticationService authenticationService;

    @PostMapping("/buy-asset")
    @Operation(summary = "Buy an asset", description = "Allows the user to buy an asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset purchased successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> buyAsset(
            @RequestBody @Valid BuyAssetRequest request,
            @Parameter(hidden = true) Authentication authentication) {
        User currentUser = authenticationService.getCurrentUser(authentication.getName());

        if ((request.getAmountInUSD() == null && request.getAmountOfCurrency() == null) ||
                (request.getAmountInUSD() != null && request.getAmountOfCurrency() != null)) {
            return ResponseEntity.badRequest().body("Please provide amount in USD or amount of Currency, not both.");
        }

        transactionService.buyAsset(request.getPortfolioid(), request.getCurrencyid(), request.getAmountInUSD(), request.getAmountOfCurrency(), currentUser);
        return ResponseEntity.ok("Asset purchased successfully");
    }

    @PostMapping("/sell-asset")
    @Operation(summary = "Sell an asset", description = "Allows the user to sell an asset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asset sold successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> sellAsset(@RequestBody @Valid SellAssetRequest request, @Parameter(hidden = true) Authentication authentication) {
        User currentUser = authenticationService.getCurrentUser(authentication.getName());

        if ((request.getAmount() == null && request.getPriceInUSD() == null) ||
                (request.getAmount() != null && request.getPriceInUSD() != null)) {
            return ResponseEntity.badRequest().body("Please provide amount in USD or amount of Currency, not both.");
        }

        transactionService.sellAsset(request.getPortfolioid(), request.getCurrencyid(), request.getAmount(), request.getPriceInUSD(), currentUser);
        return ResponseEntity.ok("Asset sold successfully");
    }

    @GetMapping("/available-assets") // DO ZMIANY
    @Operation(summary = "Get available assets", description = "Retrieves a list of available assets with their current prices")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Assets retrieved successfully")
    })
    public ResponseEntity<Page<Map<String, Object>>> getAvailableAssets(@PageableDefault(page = 0, size = 50, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Map<String, Object>> assets = transactionService.getAvailableAssetsWithPrices(pageable);
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/history")
    @Operation(summary = "Get transaction history", description = "Retrieves the transaction history for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully")
    })
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionHistory(@PageableDefault(page = 0, size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionHistory(pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/history/portfolio/{portfolioid}")
    @Operation(summary = "Get transaction history by portfolio", description = "Retrieves the transaction history for a specific portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionHistoryByPortfolio(@Parameter(description = "ID of the portfolio to retrieve transaction history") @PathVariable Integer portfolioid,
            @PageableDefault(page = 0, size = 10, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionHistoryByPortfolio(portfolioid, pageable);
        return ResponseEntity.ok(transactions);
    }
}
