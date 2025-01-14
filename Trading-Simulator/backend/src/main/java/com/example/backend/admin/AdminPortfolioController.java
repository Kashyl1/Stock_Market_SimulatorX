package com.example.backend.admin;

import com.example.backend.exceptions.ErrorResponse;
import com.example.backend.portfolio.PortfolioDTO;
import com.example.backend.portfolio.PortfolioService;
import com.example.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/admin/portfolios")
@RequiredArgsConstructor
@Tag(name = "Admin Portfolio Controller", description = "Management of user portfolios")
public class AdminPortfolioController {

    private final PortfolioService portfolioService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all portfolios", description = "Retrieve a paginated list of all portfolios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolios retrieved successfully")
    })
    public ResponseEntity<Page<PortfolioDTO>> getAllPortfolios(Pageable pageable) {
        Page<PortfolioDTO> portfolios = portfolioService.getAllPortfolios(pageable);
        return ResponseEntity.ok(portfolios);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{portfolioId}")
    @Operation(summary = "Get portfolio by ID", description = "Retrieve a portfolio by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PortfolioDTO> getPortfolioById(
            @Parameter(description = "ID of the portfolio to retrieve") @PathVariable Integer portfolioId) {
        PortfolioDTO portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get portfolios by user ID", description = "Retrieve portfolios for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolios retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<PortfolioDTO>> getPortfoliosByUserId(
            @Parameter(description = "ID of the user whose portfolios to retrieve") @PathVariable Integer userId,
            Pageable pageable) {
        Page<PortfolioDTO> portfolios = portfolioService.getPortfoliosByUserId(userId, pageable);
        return ResponseEntity.ok(portfolios);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{portfolioId}")
    @Operation(summary = "Delete a portfolio", description = "Delete a portfolio by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deletePortfolio(
            @Parameter(description = "ID of the portfolio to delete") @PathVariable Integer portfolioId) {
        portfolioService.deletePortfolioForAdmin(portfolioId);
        return ResponseEntity.ok("Portfolio has been deleted");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{portfolioId}")
    @Operation(summary = "Update a portfolio", description = "Update a portfolio's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfolio updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Portfolio not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PortfolioDTO> updatePortfolio(
            @Parameter(description = "ID of the portfolio to update") @PathVariable Integer portfolioId,
            @Parameter(description = "Data for updating the portfolio", required = true) @RequestBody UpdatePortfolioRequest request) {
        PortfolioDTO updatedPortfolio = portfolioService.updatePortfolio(portfolioId, request);
        return ResponseEntity.ok(updatedPortfolio);
    }

}
