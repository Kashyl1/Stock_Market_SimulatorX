package com.example.backend.currency;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentRateRepository extends JpaRepository<CurrentRate, Integer> {
}
