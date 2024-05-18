package com.example.backend.currency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricalRateRepository extends JpaRepository<HistoricalRate, Integer> {
}
