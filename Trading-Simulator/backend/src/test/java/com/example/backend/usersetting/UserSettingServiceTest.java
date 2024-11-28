package com.example.backend.usersetting;

import com.example.backend.mailVerification.VerificationService;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.ConfirmationTextMismatchException;
import com.example.backend.exceptions.EmailSendingException;
import com.example.backend.exceptions.InvalidPasswordException;
import com.example.backend.portfolio.Portfolio;
import com.example.backend.portfolio.PortfolioAssetRepository;
import com.example.backend.portfolio.PortfolioRepository;
import com.example.backend.transaction.TransactionRepository;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserSettingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioAssetRepository portfolioAssetRepository;

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private UserSettingService userSettingService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setFirstname("Test");
        user.setPassword("encodedPassword");
    }

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "newPassword");

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userSettingService.changePassword(request);

        verify(userRepository).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void changePassword_InvalidCurrentPassword_ShouldThrowException() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword");

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userSettingService.changePassword(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserAccount_Success() {
        String confirmText = "Delete test@example.com";
        Portfolio portfolio1 = new Portfolio();
        Portfolio portfolio2 = new Portfolio();
        List<Portfolio> portfolios = Arrays.asList(portfolio1, portfolio2);

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);
        when(portfolioRepository.findByUser(user)).thenReturn(portfolios);

        String result = userSettingService.deleteUserAccount(confirmText);

        assertEquals("User account and associated data have been deleted.", result);

        verify(transactionRepository).deleteAllByUser(user);
        verify(portfolioAssetRepository).deleteAllByPortfolio(portfolio1);
        verify(portfolioAssetRepository).deleteAllByPortfolio(portfolio2);
        verify(portfolioRepository).deleteAll(portfolios);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserAccount_ConfirmationTextMismatch_ShouldThrowException() {
        String confirmText = "Delete wrong@example.com";

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);

        assertThrows(ConfirmationTextMismatchException.class, () -> userSettingService.deleteUserAccount(confirmText));

        verify(transactionRepository, never()).deleteAllByUser(any(User.class));
        verify(portfolioAssetRepository, never()).deleteAllByPortfolio(any());
        verify(portfolioRepository, never()).deleteAll(anyList());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void changeEmail_Success() throws Exception {
        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewEmail("newemail@example.com");

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);
        when(verificationService.verificationToken()).thenReturn("verificationToken");

        ChangeEmailResponse response = userSettingService.changeEmail(request);

        verify(userRepository).save(user);
        verify(verificationService).sendVerificationEmail(user, "verificationToken");

        assertEquals("newemail@example.com", user.getEmail());
        assertFalse(user.isVerified());
        assertEquals("verificationToken", user.getVerificationToken());
        assertEquals("Email changed successfully. Please verify your new email. You will be redirected to main page in 5 seconds...", response.getMessage());
    }

    @Test
    void changeEmail_InvalidCurrentPassword_ShouldThrowException() {
        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewEmail("newemail@example.com");

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("wrongPassword", user.getPassword())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userSettingService.changeEmail(request));

        verify(userRepository, never()).save(any(User.class));
        verify(verificationService, never()).sendVerificationEmail(any(User.class), anyString());
    }

    @Test
    void changeEmail_EmailSendingFailed_ShouldThrowException() throws Exception {
        ChangeEmailRequest request = new ChangeEmailRequest();
        request.setCurrentPassword("currentPassword");
        request.setNewEmail("newemail@example.com");

        when(authenticationService.getCurrentUserEmail()).thenReturn(user.getEmail());
        when(authenticationService.getCurrentUser(user.getEmail())).thenReturn(user);
        when(passwordEncoder.matches("currentPassword", user.getPassword())).thenReturn(true);
        when(verificationService.verificationToken()).thenReturn("verificationToken");
        doThrow(new RuntimeException("Email sending failed")).when(verificationService).sendVerificationEmail(user, "verificationToken");

        assertThrows(EmailSendingException.class, () -> userSettingService.changeEmail(request));

        verify(userRepository).save(user);
        verify(verificationService).sendVerificationEmail(user, "verificationToken");
    }
}
