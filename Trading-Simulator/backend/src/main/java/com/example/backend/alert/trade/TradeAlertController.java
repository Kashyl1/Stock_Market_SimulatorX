package com.example.backend.alert.trade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts/trade")
@RequiredArgsConstructor
public class TradeAlertController {

    private final TradeAlertService tradeAlertService;

    @PostMapping("/create") // PRZYPOMINAJKA BYM DODAŁ JAKĄŚ REZERWACJE ŚRODKÓW MAM 2K DAJE BUY NA 1K TO MAM 1K PORTFEL DO PÓKI NIE ANULUJE
    public ResponseEntity<TradeAlertResponse> createTradeAlert(@Valid @RequestBody CreateTradeAlertRequest request) {
        TradeAlert tradeAlert = tradeAlertService.createTradeAlert(request);
        TradeAlertResponse response = TradeAlertResponse.fromTradeAlert(tradeAlert);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deactivate/{tradeAlertId}")
    public ResponseEntity<String> deactivateTradeAlert(@PathVariable Integer tradeAlertId) {
        tradeAlertService.deactivateTradeAlert(tradeAlertId);
        return ResponseEntity.ok("Trade alert has been deactivated.");
    }

    @DeleteMapping("/{tradeAlertId}")
    public ResponseEntity<String> deleteTradeAlert(@PathVariable Integer tradeAlertId) {
        tradeAlertService.deleteTradeAlert(tradeAlertId);
        return ResponseEntity.ok("Trade alert has been deleted.");
    }

    @GetMapping("/my-trade-alerts")
    public ResponseEntity<List<TradeAlertResponse>> getUserTradeAlerts() {
        List<TradeAlert> tradeAlerts = tradeAlertService.getUserTradeAlerts();
        List<TradeAlertResponse> responses = tradeAlerts.stream()
                .map(TradeAlertResponse::fromTradeAlert)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
