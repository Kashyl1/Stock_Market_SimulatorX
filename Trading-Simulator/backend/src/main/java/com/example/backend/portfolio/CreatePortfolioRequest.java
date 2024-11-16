package com.example.backend.portfolio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for creating a new portfolio")
public class CreatePortfolioRequest {

    @Schema(description = "Name of the new portfolio", example = "My Investment Portfolio")
    private String name;
}
