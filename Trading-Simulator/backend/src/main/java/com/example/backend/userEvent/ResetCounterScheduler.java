package com.example.backend.userEvent;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class ResetCounterScheduler {

    private final UserEventTrackingService userEventTrackingService;

    public ResetCounterScheduler(UserEventTrackingService userEventTrackingService) {
        this.userEventTrackingService = userEventTrackingService;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyCounterAtMidnight() {
        userEventTrackingService.resetDailyCount();
    }
}
