package com.example.backend.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private boolean verified;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal balance;
    private BigDecimal reservedBalance;
    private boolean blocked;
}
