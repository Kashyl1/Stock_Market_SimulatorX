package com.example.backend.currency;

import com.example.backend.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Component
@Tag(name = "Scheduled Tasks", description = "Tasks scheduled to update currency data periodically")
public class ScheduledTasks {

    private final CurrencyService currencyService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    public ScheduledTasks(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(fixedRate = 1000 * 10)
    @Operation(summary = "Update current prices", description = "Scheduled task to update current prices every 10 seconds")
    public void updateCurrentPrices() {
        currencyService.updateCurrentPrice();
    }

    @Scheduled(fixedRate = 1000 * 60 * 5)
    @Operation(summary = "Update additional data", description = "Scheduled task to update additional data every 5 minutes")
    public void updateAdditionalData() {
        currencyService.updateAdditionalData();
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    @Operation(summary = "Update currency images", description = "Scheduled task to update currency images daily")
    public void updateWeeklyCurrencyImages() {
        currencyService.updateCurrencyNamesAndImages();
    }
}
