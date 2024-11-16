package com.example.backend.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request to update a portfolio's details")
public class UpdatePortfolioRequest {
    @Schema(description = "New name for the portfolio", example = "My Updated Portfolio")
    private String name;
}
