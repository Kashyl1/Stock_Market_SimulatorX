package com.example.backend.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
        Optional<Currency> findBySymbol(String symbol);
        List<Currency> findBySymbolIn(List<String> symbols);

}
