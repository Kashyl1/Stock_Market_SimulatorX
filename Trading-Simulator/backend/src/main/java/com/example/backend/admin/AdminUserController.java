package com.example.backend.admin;

import com.example.backend.exceptions.ErrorResponse;
import com.example.backend.user.UserDTO;
import com.example.backend.user.UserService;
import com.example.backend.usersetting.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Controller", description = "Management of user accounts")
public class AdminUserController {

    private final UserService userService;
    private final UserSettingService userSettingService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a paginated list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> allUsers = userService.getAllUsers(pageable);
        return ResponseEntity.ok(allUsers);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Integer id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Update a user's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Integer id,
            @RequestBody UpdateUserRequest request) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Delete a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "ID of the user to delete") @PathVariable Integer id) {
        userSettingService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-admin")
    @Operation(summary = "Create an admin user", description = "Create a new admin user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admin user created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDTO> createAdmin(@RequestBody CreateAdminRequest request) {
        UserDTO admin = userService.createAdminUser(request);
        return ResponseEntity.ok(admin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/block")
    @Operation(summary = "Block or unblock a user", description = "Set the blocked status of a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> blockUser(
            @Parameter(description = "ID of the user to block/unblock") @PathVariable Integer id) {
        String answer = userService.setUserBlockedStatus(id);
        return ResponseEntity.ok(answer);
    }
}
