package com.example.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;


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
