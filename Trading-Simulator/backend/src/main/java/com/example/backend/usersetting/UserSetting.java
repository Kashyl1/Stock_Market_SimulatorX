package com.example.backend.usersetting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.backend.user.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UserSettings")
@Schema(description = "Entity representing user-specific settings")
public class UserSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the user setting", example = "1")
    private Integer userSettingID;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    @Schema(description = "User associated with this setting", required = true)
    private User user;

    @Column(nullable = false, length = 100)
    @Schema(description = "Name of the setting", example = "notificationPreference", required = true)
    private String settingName;

    @Column(columnDefinition = "text", nullable = false)
    @Schema(description = "Value of the setting", example = "EMAIL", required = true)
    private String settingValue;
}
