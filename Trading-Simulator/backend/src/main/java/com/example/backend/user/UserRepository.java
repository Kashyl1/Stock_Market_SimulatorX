package com.example.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByPasswordResetToken(String token);

}
