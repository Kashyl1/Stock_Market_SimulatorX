package com.example.backend.usersetting;

import org.springframework.data.jpa.repository.JpaRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Setting Repository", description = "Repository interface for UserSetting entity")
public interface UserSettingRepository extends JpaRepository<UserSetting, Integer> {
}
