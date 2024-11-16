package com.example.backend.admin;

import com.example.backend.exceptions.ErrorResponse;
import com.example.backend.transaction.TransactionHistoryDTO;
import com.example.backend.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
@Tag(name = "Admin Transaction Controller", description = "Management of user transactions")
public class AdminTransactionController {

    private final TransactionService transactionService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieve a paginated list of all transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully")
    })
    public ResponseEntity<Page<TransactionHistoryDTO>> getAllTransactions(Pageable pageable) {
        Page<TransactionHistoryDTO> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userid}")
    @Operation(summary = "Get transactions by user ID", description = "Retrieve transactions for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionsByUser(
            @Parameter(description = "ID of the user whose transactions to retrieve") @PathVariable Integer userid,
            Pageable pageable) {
        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionsByUser(userid, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/portfolio/{portfolioid}")
    @Operation(summary = "Get transactions by portfolio ID", description = "Retrieve transactions for a specific portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionsByPortfolio(
            @Parameter(description = "ID of the portfolio whose transactions to retrieve") @PathVariable Integer portfolioid,
            Pageable pageable) {
        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionsByPortfolio(portfolioid, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{transactionid}/suspicious")
    @Operation(summary = "Mark transaction as suspicious", description = "Mark or unmark a transaction as suspicious")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction updated successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> markTransactionsAsSuspicious(
            @Parameter(description = "ID of the transaction to update") @PathVariable Integer transactionid,
            @Parameter(description = "Flag to mark as suspicious") @RequestParam boolean suspicious) {
        transactionService.markTransactionAsSuspicious(transactionid, suspicious);
        String status = suspicious ? "marked as suspicious" : "unmarked as suspicious";
        return ResponseEntity.ok("Transaction has been " + status + " successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/suspicious")
    @Operation(summary = "Get suspicious transactions", description = "Retrieve transactions marked as suspicious")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suspicious transactions retrieved successfully")
    })
    public ResponseEntity<List<TransactionHistoryDTO>> getSuspiciousTransactions(
            @Parameter(description = "Threshold amount to filter suspicious transactions") @RequestParam(required = false) BigDecimal thresholdAmount) {
        if (thresholdAmount == null) {
            thresholdAmount = new BigDecimal("100000");
        }
        List<TransactionHistoryDTO> transactions = transactionService.getSuspiciousTransactions(thresholdAmount);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{transactionid}")
    @Operation(summary = "Get transaction by ID", description = "Retrieve a transaction by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransactionHistoryDTO> getTransactionById(
            @Parameter(description = "ID of the transaction to retrieve") @PathVariable Integer transactionid) {
        TransactionHistoryDTO transaction = transactionService.getTransactionById(transactionid);
        return ResponseEntity.ok(transaction);
    }
}
