package com.example.backend.currency;

import com.example.backend.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final CurrencyService currencyService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    public ScheduledTasks(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void updateCurrentPrices() {
        currencyService.updateCurrentPrice();
    }

    @Scheduled(fixedRate = 1000 * 60 * 30)
    public void updateAdditionalData() {
        currencyService.updateAdditionalData();
    }
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateWeeklyCurrencyImages() {
        currencyService.updateCurrencyNamesAndImages();
    }
}

