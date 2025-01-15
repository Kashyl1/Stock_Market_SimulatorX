package com.example.backend.portfolio;

import com.example.backend.exceptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/portfolios")
@Tag(name = "Portfolio Controller", description = "Manages user portfolios and their assets")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;
    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);

    @PostMapping("/create")
    @Operation(summary = "Create a new portfolio", description = "Creates a new portfolio for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PortfolioDTO> createPortfolio(
            @RequestBody @Valid CreatePortfolioRequest request) {
        Portfolio portfolio = portfolioService.createPortfolio(request.getName());
        PortfolioDTO portfolioDTO = new PortfolioDTO(portfolio.getPortfolioid(), portfolio.getName(), null, portfolio.getCreatedAt(), portfolio.getUpdatedAt());
        return ResponseEntity.ok(portfolioDTO);
    }

    @GetMapping("/my-portfolios")
    @Operation(summary = "Get user's portfolios", description = "Retrieves all portfolios owned by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolios retrieved successfully")
    })
    public ResponseEntity<List<PortfolioDTO>> getUserPortfolios() {
        List<PortfolioDTO> portfolios = portfolioService.getUserPortfolios();
        return ResponseEntity.ok(portfolios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific portfolio", description = "Retrieves a specific portfolio by its ID for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PortfolioDTO> getUserPortfolioByid(
            @Parameter(description = "ID of the portfolio to retrieve") @PathVariable Integer id) {
        PortfolioDTO portfolioDTO = portfolioService.getUserPortfolioByid(id);
        return ResponseEntity.ok(portfolioDTO);
    }

    @GetMapping("/{id}/gains")
    @Operation(summary = "Get portfolio assets with gains", description = "Calculates gains or losses for each asset in the portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assets with gains retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PortfolioAssetDTO>> getPortfolioAssetsWithGains(
            @Parameter(description = "ID of the portfolio to analyze") @PathVariable Integer id) {
        List<PortfolioAssetDTO> assetsWithGains = portfolioService.getPortfolioAssetsWithGains(id);
        return ResponseEntity.ok(assetsWithGains);
    }

    @GetMapping("/{id}/total-gain-or-loss")
    @Operation(summary = "Get total portfolio gain or loss", description = "Calculates the total gain or loss for the portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total gain or loss calculated successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BigDecimal> getTotalPortfolioGainOrLoss(
            @Parameter(description = "ID of the portfolio to calculate") @PathVariable Integer id) {
        BigDecimal gainOrLoss = portfolioService.calculateTotalPortfolioGainOrLoss(id);
        return ResponseEntity.ok(gainOrLoss);
    }

    @DeleteMapping("/{id}/delete-portfolio")
    @Operation(summary = "Delete portfolio with assets",
    description = "Endpoint that sells all assets in portfolio and then deletes the portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deletePortfolio(
            @Parameter(description = "ID of the portfolio to delete") @PathVariable Integer id) {
        portfolioService.deletePortfolioForUser(id);
        return ResponseEntity.ok("Portfolio has been deleted");
    }

    @GetMapping("/global-gain")
    @Operation(summary = "Get global gain/loss by user",
    description = "Endpoint that gets all user assets and calculate his global gain/loss")
    public ResponseEntity<BigDecimal> getGlobalGainByUser() {
        BigDecimal globalGainOrLoss = portfolioService.calculateUserGlobalGainLoss();
        return ResponseEntity.ok(globalGainOrLoss);
    }
}
