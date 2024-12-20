package com.example.backend.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoricalKlineRepository extends JpaRepository<HistoricalKline, Long> {
    List<HistoricalKline> findByCurrencyAndTimeIntervalOrderByOpenTimeAsc(Currency currency, String timeInterval);
    Optional<HistoricalKline> findByCurrencyAndTimeIntervalAndOpenTime(Currency currency, String timeInterval, Long openTime);

}
