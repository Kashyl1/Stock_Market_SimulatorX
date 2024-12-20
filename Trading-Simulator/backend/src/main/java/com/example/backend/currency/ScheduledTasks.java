package com.example.backend.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Scheduled(fixedRate = 1000 * 10, initialDelay = 0)
    @Operation(summary = "Update current prices", description = "Scheduled task to update current prices every 10 seconds")
    public synchronized void updateCurrentPrices() {
        currencyService.updateCurrentPrice();
    }

    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 500)
    @Operation(summary = "Update additional data", description = "Scheduled task to update additional data every 5 minutes")
    public void updateAdditionalData() {
        currencyService.updateAdditionalData();
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24, initialDelay = 1000)
    @Operation(summary = "Update currency images", description = "Scheduled task to update currency images daily")
    public void updateWeeklyCurrencyImages() {
        currencyService.updateCurrencyNamesAndImages();
    }

    @Scheduled(fixedRate = 1000 * 60, initialDelay = 2000)
    @Operation(summary = "Update data for 1-hour chart", description = "Update 1 minute kline data every 1 minute")
    public void updateOneHourChartHistoricalData() {
        currencyService.updateHistoricalData("1m", 60);
    }

    @Scheduled(fixedRate = 1000 * 60 * 3, initialDelay = 1000 * 5)
    @Operation(summary = "Update data for 3-hour chart", description = "Update 3 minute kline data every 3 minute")
    public void updateThreeHourChartHistoricalData() {
        currencyService.updateHistoricalData("3m", 60);
    }

    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 1000 * 10)
    @Operation(summary = "Update data for 6-hour chart", description = "Update 5 minute kline data every 5 minute")
    public void updateSixHourChartHistoricalData() {
        currencyService.updateHistoricalData("5m", 72);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 1000 * 15)
    @Operation(summary = "Update data for one day chart", description = "Update 30 minute kline data every 30 minute")
    public void updateOneDayChartHistoricalData() {
        currencyService.updateHistoricalData("30m", 48);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60, initialDelay = 1000 * 20)
    @Operation(summary = "Update data for one week chart", description = "Update 1 hour kline data every 1 hour")
    public void updateOneWeekChartHistoricalData() {
        currencyService.updateHistoricalData("1h", 168);
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24, initialDelay = 1000 * 25)
    @Operation(summary = "Update data for 1 month chart", description = "Update one day kline data every one day")
    public void updateOneMonthHistoricalData() {
        currencyService.updateHistoricalData("1d", 30);
    }

}
