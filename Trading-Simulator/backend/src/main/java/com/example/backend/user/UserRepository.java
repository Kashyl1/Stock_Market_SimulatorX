package com.example.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Repository", description = "Repository interface for User entity")
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    Optional<User> findByPasswordResetToken(String token);

    boolean existsByRole(Role role);
}
