package com.example.backend.userEvent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/event/user")
@RequiredArgsConstructor
@Tag(name = "User Event Public Controller", description = "Public endpoints for user events")
public class PublicUserEventController {

    private final UserEventTrackingService userEventTrackingService;

    @GetMapping("/transactions/today/count")
    @Operation(summary = "Get today's transaction count",
            description = "Retrieve the number of today's transactions (BUY_CRYPTO, SELL_CRYPTO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count of today's transactions returned successfully")
    })
    public ResponseEntity<Long> getTodayTransactionsCount() {
        long count = userEventTrackingService.getDailyTransactionCount();
        return ResponseEntity.ok(count);
    }
}
