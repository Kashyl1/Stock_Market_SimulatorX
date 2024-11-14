package com.example.backend.admin;

import com.example.backend.user.UserDTO;
import com.example.backend.user.UserService;
import com.example.backend.usersetting.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final UserSettingService userSettingService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Integer id, @RequestBody UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        userSettingService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-admin")
    public UserDTO createAdmin(@RequestBody CreateAdminRequest request) {
        return userService.createAdminUser(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/block")
    public ResponseEntity<String> blockUser(@PathVariable Integer id, @RequestBody BlockUserRequest request) {
        userService.setUserBlockedStatus(id, request.isBlocked());
        String status = request.isBlocked() ? "blocked" : "unblocked";
        return ResponseEntity.ok("User has been " + status + " successfully");
    }


}
