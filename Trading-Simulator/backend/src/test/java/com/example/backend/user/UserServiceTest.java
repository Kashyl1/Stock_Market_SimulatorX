package com.example.backend.user;

import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.InvalidAmountException;
import com.example.backend.exceptions.UserNotAuthenticatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void addFunds_ShouldAddFunds_WhenAmountIsValid() {
        BigDecimal amount = BigDecimal.valueOf(100);
        String email = "user@example.com";

        User user = new User();
        user.setEmail(email);
        user.setBalance(BigDecimal.valueOf(50));

        when(authenticationService.getCurrentUserEmail()).thenReturn(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(user);

        BalanceResponse response = userService.addFunds(amount);

        assertEquals("Funds added successfully.", response.getMessage());
        assertEquals(BigDecimal.valueOf(150), response.getBalance());
        verify(userRepository).save(user);
    }

    @Test
    void addFunds_ShouldThrowInvalidAmountException_WhenAmountIsNull() {
        BigDecimal amount = null;
        String email = "user@example.com";

        when(authenticationService.getCurrentUserEmail()).thenReturn(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(new User());

        assertThrows(InvalidAmountException.class, () -> userService.addFunds(amount));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addFunds_ShouldThrowInvalidAmountException_WhenAmountIsZero() {
        BigDecimal amount = BigDecimal.ZERO;
        String email = "user@example.com";

        when(authenticationService.getCurrentUserEmail()).thenReturn(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(new User());

        assertThrows(InvalidAmountException.class, () -> userService.addFunds(amount));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addFunds_ShouldThrowInvalidAmountException_WhenAmountIsNegative() {
        BigDecimal amount = BigDecimal.valueOf(-50);
        String email = "user@example.com";

        when(authenticationService.getCurrentUserEmail()).thenReturn(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(new User());

        assertThrows(InvalidAmountException.class, () -> userService.addFunds(amount));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getBalance_ShouldReturnBalance() {
        String email = "user@example.com";

        User user = new User();
        user.setEmail(email);
        user.setBalance(BigDecimal.valueOf(200));

        when(authenticationService.getCurrentUserEmail()).thenReturn(email);
        when(authenticationService.getCurrentUser(email)).thenReturn(user);

        BalanceResponse response = userService.getBalance();

        assertEquals("Balance fetched successfully.", response.getMessage());
        assertEquals(BigDecimal.valueOf(200), response.getBalance());
    }

    @Test
    void addFunds_ShouldThrowUserNotAuthenticatedException_WhenUserIsNotAuthenticated() {
        when(authenticationService.getCurrentUserEmail()).thenThrow(new UserNotAuthenticatedException("User not authenticated"));

        assertThrows(UserNotAuthenticatedException.class, () -> userService.addFunds(BigDecimal.valueOf(100)));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getBalance_ShouldThrowUserNotAuthenticatedException_WhenUserIsNotAuthenticated() {
        when(authenticationService.getCurrentUserEmail()).thenThrow(new UserNotAuthenticatedException("User not authenticated"));

        assertThrows(UserNotAuthenticatedException.class, () -> userService.getBalance());
    }
}
