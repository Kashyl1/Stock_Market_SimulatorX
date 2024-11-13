package com.example.backend.user;

import com.example.backend.admin.CreateAdminRequest;
import com.example.backend.admin.UpdateUserRequest;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.EmailAlreadyExistsException;
import com.example.backend.exceptions.InvalidAmountException;
import com.example.backend.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public BalanceResponse addFunds(BigDecimal amount) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        return BalanceResponse.builder()
                .message("Funds added successfully.")
                .balance(user.getBalance())
                .build();
    }

    public BalanceResponse getBalance() {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        return BalanceResponse.builder()
                .balance(user.getBalance())
                .message("Balance fetched successfully.")
                .build();
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .verified(user.isVerified())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .balance(user.getBalance())
                .reservedBalance(user.getReservedBalance())
                .blocked(user.isBlocked())
                .build());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .verified(user.isVerified())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .balance(user.getBalance())
                .reservedBalance(user.getReservedBalance())
                .blocked(user.isBlocked())
                .build();
    }

    @Transactional
    public UserDTO updateUser(Integer id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getFirstname() != null) {
            user.setFirstname(request.getFirstname());
        }
        if (request.getLastname() != null) {
            user.setLastname(request.getLastname());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        user.setVerified(request.isVerified());

        userRepository.save(user);

        return UserDTO.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .verified(user.isVerified())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .balance(user.getBalance())
                .reservedBalance(user.getReservedBalance())
                .blocked(user.isBlocked())
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createAdminUser(CreateAdminRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User admin = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_ADMIN)
                .verified(true)
                .balance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO)
                .reservedBalance(request.getReservedBalance() != null ? request.getReservedBalance() : BigDecimal.ZERO)
                .build();

        userRepository.save(admin);

        return UserDTO.builder()
                .id(admin.getId())
                .firstname(admin.getFirstname())
                .lastname(admin.getLastname())
                .email(admin.getEmail())
                .verified(admin.isVerified())
                .role(admin.getRole())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .balance(admin.getBalance())
                .reservedBalance(admin.getReservedBalance())
                .build();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void setUserBlockedStatus(Integer id, boolean blocked) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setBlocked(blocked);
        userRepository.save(user);
    }


}
