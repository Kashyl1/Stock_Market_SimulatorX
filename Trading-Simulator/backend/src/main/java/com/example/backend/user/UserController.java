package com.example.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint do doładowania środków użytkownika
     */
    @PostMapping("/add-funds")
    public ResponseEntity<BalanceResponse> addFunds(@RequestBody AddFundsRequest request) {
        BalanceResponse response = userService.addFunds(request.getAmount());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance() {
        BalanceResponse response = userService.getBalance();
        return ResponseEntity.ok(response);
    }
}
