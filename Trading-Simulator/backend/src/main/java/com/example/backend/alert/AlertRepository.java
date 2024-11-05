package com.example.backend.alert;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
    List<Alert> findByActiveTrue();
    List<Alert> findByUser(User user);
}
