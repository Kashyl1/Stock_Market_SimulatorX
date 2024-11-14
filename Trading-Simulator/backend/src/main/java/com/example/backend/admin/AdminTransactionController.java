package com.example.backend.admin;

import com.example.backend.transaction.Transaction;
import com.example.backend.transaction.TransactionHistoryDTO;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<TransactionHistoryDTO>> getAllTransactions(Pageable pageable) {
        Page<TransactionHistoryDTO> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userid}")
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionsByUser(@PathVariable Integer userid, Pageable pageable) {
        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionsByUser(userid, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/portfolio/{portfolioid}")
    public ResponseEntity<Page<TransactionHistoryDTO>> getTransactionsByPortfolio(@PathVariable Integer portfolioid, Pageable pageable) {
        Page<TransactionHistoryDTO> transactions = transactionService.getTransactionsByPortfolio(portfolioid, pageable);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{transactionid}/suspicious")
    public ResponseEntity<String> markTransactionsAsSuspicious(@PathVariable Integer transactionid, @RequestParam boolean suspicious) {
        transactionService.markTransactionAsSuspicious(transactionid, suspicious);
        String status = suspicious ? "Marked as suspicious" : "Unmarked as suspicious";
        return ResponseEntity.ok("Transaction has been " + status + " successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/suspicious")
    public ResponseEntity<List<TransactionHistoryDTO>> getSuspiciousTransactions(@RequestParam(required = false) BigDecimal thresholdAmount) {
        if (thresholdAmount == null) {
            thresholdAmount = new BigDecimal("100000");
        }
        List<TransactionHistoryDTO> transactions = transactionService.getSuspiciousTransactions(thresholdAmount);
        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{transactionid}")
    public ResponseEntity<TransactionHistoryDTO> getTransactionById(@PathVariable Integer transactionid) {
        TransactionHistoryDTO transaction = transactionService.getTransactionById(transactionid);
        return ResponseEntity.ok(transaction);
    }
}
