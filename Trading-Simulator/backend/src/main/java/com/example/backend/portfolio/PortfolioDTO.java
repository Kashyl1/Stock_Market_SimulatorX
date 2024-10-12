package com.example.backend.portfolio;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDTO {
    private Integer portfolioid;
    private String name;
    private List<PortfolioAssetDTO> portfolioAssets;
}
