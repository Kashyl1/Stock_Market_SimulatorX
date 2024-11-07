package com.example.backend.alert.trade;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeAlertRepository extends JpaRepository<TradeAlert, Integer> {
    List<TradeAlert> findByActiveTrue();
    List<TradeAlert> findByUser(User user);
}
