package com.example.backend.admin;

import com.example.backend.user.Role;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstname;
    private String lastname;
    private boolean verified;
    private Role role;
}
