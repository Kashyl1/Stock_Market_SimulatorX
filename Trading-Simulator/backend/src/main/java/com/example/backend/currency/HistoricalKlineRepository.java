package com.example.backend.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoricalKlineRepository extends JpaRepository<HistoricalKline, Long> {
    List<HistoricalKline> findByCurrencyAndIntervalOrderByOpenTimeAsc(Currency currency, String interval);
    boolean existsByCurrencyAndIntervalAndOpenTime(Currency currency, String interval, Long openTime);
    Optional<HistoricalKline> findByCurrencyAndIntervalAndOpenTime(Currency currency, String interval, Long openTime);

}
