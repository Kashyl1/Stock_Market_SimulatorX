package com.example.backend.admin;

import com.example.backend.user.Role;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAdminRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private BigDecimal balance;
    private BigDecimal reservedBalance;
}
