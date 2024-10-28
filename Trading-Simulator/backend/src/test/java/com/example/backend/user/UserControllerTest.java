package com.example.backend.user;

import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.exceptions.GlobalExceptionHandler;
import com.example.backend.exceptions.InvalidAmountException;
import com.example.backend.exceptions.UserNotAuthenticatedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void addFunds_ShouldReturnBalanceResponse_WhenAmountIsValid() throws Exception {
        BigDecimal amount = BigDecimal.valueOf(100);
        BalanceResponse response = BalanceResponse.builder()
                .message("Funds added successfully.")
                .balance(BigDecimal.valueOf(200))
                .build();

        when(userService.addFunds(amount)).thenReturn(response);

        mockMvc.perform(post("/api/user/add-funds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Funds added successfully."))
                .andExpect(jsonPath("$.balance").value(200));

        verify(userService).addFunds(amount);
    }

    @Test
    void addFunds_ShouldReturnBadRequest_WhenAmountIsInvalid() throws Exception {
        when(userService.addFunds(any(BigDecimal.class))).thenThrow(new InvalidAmountException("Amount must be greater than zero."));

        mockMvc.perform(post("/api/user/add-funds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Amount must be greater than zero."))
                .andExpect(jsonPath("$.status").value(400));

        verify(userService).addFunds(BigDecimal.valueOf(0));
    }

    @Test
    void getBalance_ShouldReturnBalanceResponse() throws Exception {
        BalanceResponse response = BalanceResponse.builder()
                .message("Balance fetched successfully.")
                .balance(BigDecimal.valueOf(150))
                .build();

        when(userService.getBalance()).thenReturn(response);

        mockMvc.perform(get("/api/user/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Balance fetched successfully."))
                .andExpect(jsonPath("$.balance").value(150));

        verify(userService).getBalance();
    }

    @Test
    void getBalance_ShouldReturnUnauthorized_WhenUserNotAuthenticated() throws Exception {
        when(userService.getBalance()).thenThrow(new UserNotAuthenticatedException("User not authenticated"));

        mockMvc.perform(get("/api/user/balance"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("User not authenticated"))
                .andExpect(jsonPath("$.status").value(401));

        verify(userService).getBalance();
    }
}
