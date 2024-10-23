package com.example.backend.usersetting;

import com.example.backend.MailVerification.VerificationService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAsset;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSettingService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final VerificationService verificationService;

    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ChangePasswordResponse.builder()
                    .message("Current password is incorrect")
                    .build();
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ChangePasswordResponse.builder()
                .message("Password changed successfully")
                .build();
    }
    @Transactional
    public String deleteUserAccount(String confirmText) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        String expectedText = "Delete " + user.getEmail();
        if (!confirmText.equals(expectedText)) {
            return "Confirmation text is incorrect.";
        }

        transactionRepository.deleteAllByUser(user);

        List<Portfolio> portfolios = portfolioRepository.findByUser(user);
        for (Portfolio portfolio : portfolios) {
            portfolioAssetRepository.deleteAllByPortfolio(portfolio);
        }
        portfolioRepository.deleteAll(portfolios);
        userRepository.delete(user);

        return "User account and associated data have been deleted.";
    }

    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {
        String currentEmail = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(currentEmail);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ChangeEmailResponse.builder()
                    .message("Current password is incorrect")
                    .build();
        }

        String newEmail = request.getNewEmail();
        user.setEmail(newEmail);
        user.setVerified(false);

        String verificationToken = verificationService.verificationToken();
        user.setVerificationToken(verificationToken);

        userRepository.save(user);

        try {
            verificationService.sendVerificationEmail(user, verificationToken);
        } catch (Exception e) {
            return ChangeEmailResponse.builder()
                    .message("Failed to send verification email")
                    .build();
        }

        return ChangeEmailResponse.builder()
                .message("Email changed successfully. Please verify your new email. You will be redirected to main page in 5 seconds...")
                .build();
    }

}
