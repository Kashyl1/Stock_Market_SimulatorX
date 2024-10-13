package com.example.backend.user;

import com.example.backend.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    /**
     * Dodawanie środków do konta użytkownika
     */
    @Transactional
    public BalanceResponse addFunds(double amount) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (amount <= 0) {
            return BalanceResponse.builder()
                    .message("Amount must be greater than zero.")
                    .balance(user.getBalance())
                    .build();
        }

        user.setBalance(user.getBalance() + amount);
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
