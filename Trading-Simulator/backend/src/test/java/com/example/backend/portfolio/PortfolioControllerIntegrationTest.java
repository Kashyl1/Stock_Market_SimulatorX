package com.example.backend.portfolio;

import com.example.backend.auth.AuthenticationRequest;
import com.example.backend.auth.AuthenticationResponse;
import com.example.backend.auth.RegisterRequest;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class PortfolioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String userEmail = "testuser@example.com";
    private String userPassword = "Test@123";

    @BeforeEach
    public void setup() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .firstname("Test")
                .lastname("User")
                .email(userEmail)
                .password(userPassword)
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(userEmail).orElseThrow();
        user.setVerified(true);
        userRepository.save(user);
    }

    @Test
    public void testCreatePortfolio() throws Exception {
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email(userEmail)
                .password(userPassword)
                .build();

        String authResponse = mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthenticationResponse tokenResponse = objectMapper.readValue(authResponse, AuthenticationResponse.class);
        String token = tokenResponse.getToken();

        CreatePortfolioRequest portfolioRequest = CreatePortfolioRequest.builder()
                .name("My Portfolio")
                .build();

        mockMvc.perform(post("/api/portfolios/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioRequest)))
                .andExpect(status().isOk());
    }
}
