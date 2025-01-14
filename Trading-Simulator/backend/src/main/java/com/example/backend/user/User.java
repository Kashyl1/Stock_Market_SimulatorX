package com.example.backend.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Entity representing a user in the system")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    @Schema(description = "Unique identifier of the user", example = "1")
    private Integer id;

    @Column(nullable = false, length = 50)
    @Schema(description = "First name of the user", example = "John", required = true)
    private String firstname;

    @Column(nullable = false, length = 50)
    @Schema(description = "Last name of the user", example = "Doe", required = true)
    private String lastname;

    @Column(unique = true, nullable = false, length = 100)
    @Schema(description = "Email address of the user", example = "john.doe@example.com", required = true)
    private String email;

    @Column(nullable = false, length = 256)
    @Schema(description = "Password of the user", required = true)
    private String password;

    @Column(nullable = false)
    @Schema(description = "Verification status of the account", example = "false")
    private boolean verified;

    @Schema(description = "Verification token for the account")
    private String verificationToken;

    @Schema(description = "Password reset token")
    private String passwordResetToken;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Role of the user in the system", example = "ROLE_USER")
    private Role role;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Schema(description = "Timestamp when the account was created", example = "2023-10-01T12:00:00")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Schema(description = "Timestamp when the account was last updated", example = "2023-10-05T15:30:00")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 3)
    @Schema(description = "User's account balance", example = "500.00")
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 3)
    @Schema(description = "User's reserved balance", example = "0.00")
    private BigDecimal reservedBalance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Schema(description = "Account blocked status", example = "false")
    private boolean blocked = false;

    @Override
    @Schema(hidden = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    @Schema(hidden = true)
    public String getPassword() {
        return password;
    }

    @Override
    @Schema(hidden = true)
    public String getUsername() {
        return email;
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isAccountNonLocked() {
        return !blocked;
    }

    @Override
    @Schema(hidden = true)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true)
    public boolean isEnabled() {
        return true;
    }

}
