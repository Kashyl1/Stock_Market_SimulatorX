package com.example.backend.user;

import com.example.backend.admin.CreateAdminRequest;
import com.example.backend.admin.UpdateUserRequest;
import com.example.backend.adminEvent.AdminEvent;
import com.example.backend.adminEvent.AdminEventTrackingService;
import com.example.backend.userEvent.UserEventTrackingService;
import com.example.backend.userEvent.UserEvent;
import com.example.backend.auth.AuthenticationService;
import com.example.backend.exceptions.EmailAlreadyExistsException;
import com.example.backend.exceptions.InvalidAmountException;
import com.example.backend.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Tag(name = "User Service", description = "Service containing business logic for user operations")
public class UserService {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEventTrackingService userEventTrackingService;
    private final AdminEventTrackingService adminEventTrackingService;

    @Transactional
    @Operation(summary = "Add funds to account", description = "Adds a specified amount to the logged-in user's balance")
    public BalanceResponse addFunds(BigDecimal amount) {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }

        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);

        Map<String, Object> details = Map.of("amount", amount);
        userEventTrackingService.logEvent(email, UserEvent.EventType.DEPOSIT_FUNDS, details);


        return BalanceResponse.builder()
                .message("Funds have been added successfully.")
                .balance(user.getBalance())
                .build();
    }

    @Operation(summary = "Get account balance", description = "Returns the current balance of the logged-in user")
    public BalanceResponse getBalance() {
        String email = authenticationService.getCurrentUserEmail();
        User user = authenticationService.getCurrentUser(email);

        return BalanceResponse.builder()
                .balance(user.getBalance())
                .message("Balance retrieved successfully.")
                .build();
    }

    @Transactional()
    @Operation(summary = "Get all users (admin)", description = "Returns a list of all users (admin only)")
    public Page<UserDTO> getAllUsers(Pageable pageable) {

        String adminEmail = authenticationService.getCurrentUserEmail();
        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.GET_ALL_USERS);

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

    @Transactional()
    @Operation(summary = "Get user by ID (admin)", description = "Returns user data based on ID (admin only)")
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String adminEmail = authenticationService.getCurrentUserEmail();
        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.GET_USER_BY_ID);

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
    @Operation(summary = "Update user (admin)", description = "Updates user data (admin only)")
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

        User savedUser = userRepository.save(user);

        String adminEmail = authenticationService.getCurrentUserEmail();

        Map<String, Object> details = Map.of(
                "userId", savedUser.getId(),
                "userEmail", savedUser.getEmail(),
                "Name", savedUser.getFirstname(),
                "lastName", savedUser.getLastname()
        );

        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.UPDATE_USER, details);

        return UserDTO.builder()
                .id(savedUser.getId())
                .firstname(savedUser.getFirstname())
                .lastname(savedUser.getLastname())
                .email(savedUser.getEmail())
                .verified(savedUser.isVerified())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .balance(savedUser.getBalance())
                .reservedBalance(savedUser.getReservedBalance())
                .blocked(savedUser.isBlocked())
                .build();
    }

    @Transactional
    @Operation(summary = "Create admin account (admin)", description = "Creates a new user with admin role (admin only)")
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

        User savedAdmin = userRepository.save(admin);
        String adminEmail = authenticationService.getCurrentUserEmail();
        Map<String, Object> details = Map.of(
                "createdAdminId", savedAdmin.getId(),
                "createdAdminEmail", savedAdmin.getEmail(),
                "createdAdminName", savedAdmin.getFirstname(),
                "createdAdminLastName", savedAdmin.getLastname()
        );

        adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.CREATE_ADMIN, details);

        return UserDTO.builder()
                .id(savedAdmin.getId())
                .firstname(savedAdmin.getFirstname())
                .lastname(savedAdmin.getLastname())
                .email(savedAdmin.getEmail())
                .verified(savedAdmin.isVerified())
                .role(savedAdmin.getRole())
                .createdAt(savedAdmin.getCreatedAt())
                .updatedAt(savedAdmin.getUpdatedAt())
                .balance(savedAdmin.getBalance())
                .reservedBalance(savedAdmin.getReservedBalance())
                .blocked(savedAdmin.isBlocked())
                .build();
    }

    @Transactional
    @Operation(summary = "Change user blocked status (admin)", description = "Sets the blocked status for a specified user (admin only)")
    public String setUserBlockedStatus(Integer id, boolean blocked) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String adminEmail = authenticationService.getCurrentUserEmail();

        Map<String, Object> details = Map.of(
                "userId", user.getId(),
                "userEmail", user.getEmail(),
                "Name", user.getFirstname(),
                "lastName", user.getLastname()
        );

        if (user.isBlocked()) {
            user.setBlocked(false);
            adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.UNBLOCK_USER, details);
            userRepository.save(user);
            return "User has been unblocked successfully";
        } else {
            user.setBlocked(true);
            adminEventTrackingService.logEvent(adminEmail, AdminEvent.EventType.BLOCK_USER, details);
            userRepository.save(user);
            return "User has been blocked successfully";
        }
    }
}
