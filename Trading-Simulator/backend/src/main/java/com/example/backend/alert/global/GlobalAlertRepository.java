package com.example.backend.alert.global;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GlobalAlertRepository extends JpaRepository<GlobalAlert, Integer> {
    List<GlobalAlert> findAllByActiveTrue();
    GlobalAlert findFirstByActiveTrueOrderByCreatedAtDesc();
}
