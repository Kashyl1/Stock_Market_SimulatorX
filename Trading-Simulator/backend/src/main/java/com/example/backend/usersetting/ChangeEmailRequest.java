package com.example.backend.usersetting;

import lombok.Data;

@Data
public class ChangeEmailRequest {
    private String currentPassword;
    private String newEmail;
}
