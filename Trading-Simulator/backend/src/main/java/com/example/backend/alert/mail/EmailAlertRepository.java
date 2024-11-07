package com.example.backend.alert.mail;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailAlertRepository extends JpaRepository<EmailAlert, Integer> {
    List<EmailAlert> findByActiveTrue();
    List<EmailAlert> findByUser(User user);
}
