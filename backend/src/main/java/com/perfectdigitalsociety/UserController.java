package com.perfectdigitalsociety.controller;

import com.perfectdigitalsociety.dto.request.LoginRequest;
import com.perfectdigitalsociety.dto.request.UpdateProfileRequest;
import com.perfectdigitalsociety.dto.request.UserRegistrationRequest;
import com.perfectdigitalsociety.dto.response.AuthResponse;
import com.perfectdigitalsociety.dto.response.UserBalanceResponse;
import com.perfectdigitalsociety.dto.response.UserProfileResponse;
import com.perfectdigitalsociety.dto.response.UserResponse;
import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.mapper.UserMapper;
import com.perfectdigitalsociety.security.JwtTokenProvider;
import com.perfectdigitalsociety.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "User Management", description = "User registration, authentication and profile management endpoints")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * User registration endpoint
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("User registration request for username: {}", request.getUsername());

        try {
            UserResponse response = userService.registerUser(request);
            log.info("User registration successful for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("User registration failed for username: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * User authentication endpoint
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        log.info("User login request for: {}", request.getUsername());

        try {
            User user = userService.authenticateUser(request.getUsername(), request.getPassword());

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getUsername());
            LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24 hour expiration

            // Create auth response
            AuthResponse authResponse = new AuthResponse();
            authResponse.setToken(token);
            UserProfileResponse userProfile = userService.getUserProfile(user.getId());
            UserResponse userResponse = userMapper.profileToUserResponse(userProfile);
            authResponse.setUser(userResponse);
            authResponse.setExpiresAt(expiresAt);

            log.info("User login successful for: {}", request.getUsername());
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            log.error("User login failed for: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Get user profile by ID
     * GET /api/users/profile/{id}
     */
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        log.info("Get user profile request for ID: {}", id);

        try {
            UserProfileResponse response = userService.getUserProfile(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get user profile for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Update user profile
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {

        log.info("Update profile request for user: {}", userDetails.getUsername());

        try {
            // Get current user ID from token
            Long userId = getCurrentUserId(userDetails.getUsername());

            UserResponse response = userService.updateProfile(userId, request);
            log.info("Profile update successful for user: {}", userDetails.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Profile update failed for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get user balance scores by ID
     * GET /api/users/balance/{id}
     */
    @GetMapping("/balance/{id}")
    public ResponseEntity<UserBalanceResponse> getUserBalance(@PathVariable Long id) {
        log.info("Get user balance request for ID: {}", id);

        try {
            UserBalanceResponse response = userService.calculateUserBalance(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get user balance for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get current user profile (authenticated user)
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get current user profile request for: {}", userDetails.getUsername());

        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            UserProfileResponse response = userService.getUserProfile(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get current user profile for: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Get current user balance (authenticated user)
     * GET /api/users/my-balance
     */
    @GetMapping("/my-balance")
    public ResponseEntity<UserBalanceResponse> getCurrentUserBalance(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get current user balance request for: {}", userDetails.getUsername());

        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            UserBalanceResponse response = userService.calculateUserBalance(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get current user balance for: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Logout user (invalidate token)
     * POST /api/users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("User logout request for: {}", userDetails.getUsername());

        // In a full implementation, you would add token to a blacklist
        // For now, just log the logout
        log.info("User logout successful for: {}", userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // Helper method to get current user ID
    private Long getCurrentUserId(String username) {
        // This would typically be implemented by looking up the user by username
        // For now, returning a placeholder - this should be replaced with actual implementation
        return userService.getUserByUsername(username).getId();
    }
}

