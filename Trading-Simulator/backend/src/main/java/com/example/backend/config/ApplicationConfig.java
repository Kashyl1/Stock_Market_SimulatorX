package com.example.backend.config;

import com.example.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@RequiredArgsConstructor
@Tag(name = "Application Configuration", description = "Configuration for authentication and password encoding")
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    @Tag(name = "User Details Service", description = "Loads user-specific data")
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    @Tag(name = "Authentication Provider", description = "Authentication provider for DaoAuthenticationProvider")
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    @Tag(name = "Authentication Manager", description = "Manages authentication")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @Tag(name = "Password Encoder", description = "Encodes passwords")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
