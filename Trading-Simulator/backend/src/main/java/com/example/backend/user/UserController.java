package com.example.backend.user;

import com.example.backend.exceptions.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Endpoints related to user operations")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/add-funds")
    @Operation(summary = "Add funds to account", description = "Allows the user to add funds to their account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds have been added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid amount", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BalanceResponse> addFunds(
            @RequestBody AddFundsRequest request) {
        BalanceResponse response = userService.addFunds(request.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    @Operation(summary = "Get account balance", description = "Returns the current account balance of the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully")
    })
    public ResponseEntity<BalanceResponse> getBalance() {
        BalanceResponse response = userService.getBalance();
        return ResponseEntity.ok(response);
    }
}
