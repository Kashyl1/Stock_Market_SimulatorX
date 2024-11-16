package com.example.backend.portfolio;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for portfolios")
public class PortfolioDTO {

    @Schema(description = "Unique identifier of the portfolio", example = "1")
    private Integer portfolioid;

    @Schema(description = "Name of the portfolio", example = "Retirement Fund")
    private String name;

    @Schema(description = "List of assets in the portfolio")
    private List<PortfolioAssetDTO> portfolioAssets;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    @Schema(description = "Timestamp when the portfolio was created", example = "2023-10-01T12:00")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    @Schema(description = "Timestamp when the portfolio was last updated", example = "2023-10-05T15:30")
    private LocalDateTime updatedAt;
}
