package com.example.backend.usersetting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeEmailResponse {
    private String message;
}
