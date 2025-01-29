package com.example.backend.currency;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@Profile("!test")
@Component
@Tag(name = "Scheduled Tasks", description = "Tasks scheduled to update currency data periodically")
public class ScheduledTasks {

    private final CurrencyService currencyService;

    public ScheduledTasks(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(fixedRate = 1000 * 25, initialDelay = 0)
    @Operation(summary = "Update current prices", description = "Scheduled task to update current prices every 10 seconds")
    public synchronized void updateCurrentPrices() {
        currencyService.updateCurrentPrice();
    }

    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 2000)
    @Operation(summary = "Update additional data", description = "Scheduled task to update additional data every 5 minutes")
    public void updateAdditionalData() {
        currencyService.updateAdditionalData();
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24, initialDelay = 5000)
    @Operation(summary = "Update currency images", description = "Scheduled task to update currency images daily")
    public void updateWeeklyCurrencyImages() {
        currencyService.updateCurrencyNamesAndImages();
    }

    @Scheduled(fixedRate = 1000 * 60 * 2, initialDelay = 1000 * 15)
    @Operation(summary = "Update data for 1m", description = "Update 1 minute kline data every 1 minute")
    public void updateOneHourChartHistoricalData() {
        currencyService.updateHistoricalData("1m", 1000);
    }

    @Scheduled(fixedRate = 1000 * 60 * 6, initialDelay = 1000 * 30)
    @Operation(summary = "Update data for 3m kline", description = "Update 3 minute kline data every 3 minute")
    public void updateThreeHourChartHistoricalData() {
        currencyService.updateHistoricalData("3m", 500);
    }

    @Scheduled(fixedRate = 1000 * 60 * 10, initialDelay = 1000 * 45)
    @Operation(summary = "Update data for 5m klines", description = "Update 5 minute kline data every 5 minute")
    public void updateSixHourChartHistoricalData() {
        currencyService.updateHistoricalData("5m", 500);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2, initialDelay = 1000 * 60)
    @Operation(summary = "Update data for one 30m klines", description = "Update 30 minute kline data every 30 minute")
    public void updateOneDayChartHistoricalData() {
        currencyService.updateHistoricalData("30m", 500);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 2, initialDelay = 1000 * 60 * 2)
    @Operation(summary = "Update data for 1h klines", description = "Update 1 hour kline data every 1 hour")
    public void updateOneWeekChartHistoricalData() {
        currencyService.updateHistoricalData("1h", 500);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 2, initialDelay = 1000 * 60 * 5)
    @Operation(summary = "Update data for 1 day klines", description = "Update one day kline data every one day")
    public void updateOneMonthHistoricalData() {
        currencyService.updateHistoricalData("1d", 365);
    }

}
