package com.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Tag(name = "Security Configuration", description = "Configuration for security and authorization")
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    @Tag(name = "Security Filter Chain", description = "Configures the security filter chain")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/",
                                "/index.html",
                                "/static/**",
                                "/favicon.ico",
                                "/manifest.json",
                                "/logo192.png",
                                "/logo512.png",
                                "/asset-manifest.json",
                                "/robots.txt",
                                "/{spring:[\\w-]+}",
                                "/**/{spring:[\\w-]+}",
                                "/googlee83276a39faa6454.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/portfolios/**",
                                "/api/transactions/**",
                                "/api/user/balance",
                                "/api/user-settings/**",
                                "/api/user/add-funds",
                                "/api/alerts/**",
                                "/api/analytics/**",
                                "/api/charts/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(EndpointRequest.to("health", "info", "caches", "metrics")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
