package com.example.backend.user;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.InvalidAmountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Transactional
    public BalanceResponse addFunds(BigDecimal amount) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        return BalanceResponse.builder()
                .message("Funds added successfully.")
                .balance(user.getBalance())
                .build();
    }

    public BalanceResponse getBalance() {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        return BalanceResponse.builder()
                .balance(user.getBalance())
                .message("Balance fetched successfully.")
                .build();
    }
}
