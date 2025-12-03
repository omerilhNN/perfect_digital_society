package com.perfectdigitalsociety.service;

import com.perfectdigitalsociety.dto.request.UpdateProfileRequest;
import com.perfectdigitalsociety.dto.request.UserRegistrationRequest;
import com.perfectdigitalsociety.dto.response.UserBalanceResponse;
import com.perfectdigitalsociety.dto.response.UserProfileResponse;
import com.perfectdigitalsociety.dto.response.UserResponse;
import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.exception.UserAlreadyExistsException;
import com.perfectdigitalsociety.exception.UserNotFoundException;
import com.perfectdigitalsociety.mapper.UserMapper;
import com.perfectdigitalsociety.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BalanceService balanceService;
    
    // Business Logic Methods as specified in documentation
    
    /**
     * Register a new user
     */
    public UserResponse registerUser(UserRegistrationRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());
        
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        
        // Create new user entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(User.Role.USER);
        user.setFreedomScore(50); // Initial score
        user.setSecurityScore(50); // Initial score
        user.setReputationScore(0);
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        // Trigger initial balance calculation
        balanceService.calculateUserBalance(savedUser.getId());
        
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return userMapper.toUserResponse(savedUser);
    }
    
    /**
     * Authenticate user
     */
    public User authenticateUser(String username, String password) {
        log.info("Authenticating user: {}", username);
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
            
            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            log.info("User authenticated successfully: {}", username);
            return user;
            
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", username);
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    /**
     * Update user profile
     */
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        User savedUser = userRepository.save(user);
        
        log.info("Profile updated successfully for user ID: {}", userId);
        return userMapper.toUserResponse(savedUser);
    }
    
    /**
     * Calculate user balance scores
     */
    public UserBalanceResponse calculateUserBalance(Long userId) {
        log.info("Calculating balance for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Calculate balance based on user activity, messages, and community interaction
        Integer messageCount = userRepository.findById(userId)
            .map(u -> u.getMessages().size())
            .orElse(0);
        
        // Dynamic balance calculation algorithm
        Integer newFreedomScore = calculateFreedomScore(user, messageCount);
        Integer newSecurityScore = calculateSecurityScore(user, messageCount);
        Integer newReputationScore = calculateReputationScore(user);
        
        // Update user scores
        user.setFreedomScore(newFreedomScore);
        user.setSecurityScore(newSecurityScore);
        user.setReputationScore(newReputationScore);
        
        userRepository.save(user);
        
        // Calculate balance ratio
        Double balanceRatio = calculateBalanceRatio(newFreedomScore, newSecurityScore);
        
        UserBalanceResponse response = new UserBalanceResponse();
        response.setUserId(userId);
        response.setUsername(user.getUsername());
        response.setFreedomScore(newFreedomScore);
        response.setSecurityScore(newSecurityScore);
        response.setReputationScore(newReputationScore);
        response.setBalanceRatio(balanceRatio);
        response.setLastUpdated(LocalDateTime.now());
        
        log.info("Balance calculated for user ID: {}, Freedom: {}, Security: {}", 
                userId, newFreedomScore, newSecurityScore);
        
        return response;
    }
    
    // Additional service methods
    
    public UserProfileResponse getUserProfile(Long userId) {
        log.info("Getting profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        return userMapper.toUserProfileResponse(user);
    }
    
    public List<UserResponse> getActiveUsers() {
        log.info("Getting all active users");
        return userRepository.findAllActiveUsers()
            .stream()
            .map(userMapper::toUserResponse)
            .toList();
    }
    
    public List<UserResponse> getUsersByRole(User.Role role) {
        log.info("Getting users by role: {}", role);
        return userRepository.findActiveUsersByRole(role)
            .stream()
            .map(userMapper::toUserResponse)
            .toList();
    }
    
    public void deactivateUser(Long userId, String reason) {
        log.info("Deactivating user ID: {} with reason: {}", userId, reason);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("User deactivated successfully: {}", userId);
    }
    
    public void activateUser(Long userId) {
        log.info("Activating user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        user.setIsActive(true);
        userRepository.save(user);
        
        log.info("User activated successfully: {}", userId);
    }
    
    public User getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    public boolean hasAdminRights(Long userId) {
        log.info("Checking admin rights for user ID: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getRole() == User.Role.ADMIN;
    }

    public boolean hasModeratorRights(Long userId) {
        log.info("Checking moderator rights for user ID: {}", userId);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getRole() == User.Role.MODERATOR || user.getRole() == User.Role.ADMIN;
    }

    // Private helper methods for balance calculation
    
    private Integer calculateFreedomScore(User user, Integer messageCount) {
        // Dynamic freedom score calculation based on user activity and community rules
        Integer baseScore = user.getFreedomScore() != null ? user.getFreedomScore() : 50;

        // Increase freedom score based on positive community interaction
        Integer activityBonus = Math.min(messageCount * 2, 30);
        
        // Apply reputation modifier
        Integer reputationModifier = user.getReputationScore() != null ? 
            user.getReputationScore() / 10 : 0;
        
        Integer newScore = baseScore + activityBonus + reputationModifier;
        
        // Ensure score stays within bounds (0-100)
        return Math.max(0, Math.min(100, newScore));
    }
    
    private Integer calculateSecurityScore(User user, Integer messageCount) {
        // Dynamic security score calculation
        Integer baseScore = user.getSecurityScore() != null ? user.getSecurityScore() : 50;
        
        // Security score influenced by account age and stability
        Long daysSinceCreation = user.getCreatedAt() != null ? 
            java.time.Duration.between(user.getCreatedAt(), LocalDateTime.now()).toDays() : 0;
        
        Integer stabilityBonus = Math.min((int) (daysSinceCreation / 7), 20);
        
        // Moderate activity increases security
        Integer activityModifier = messageCount > 0 && messageCount < 100 ? 10 : 0;
        
        Integer newScore = baseScore + stabilityBonus + activityModifier;
        
        return Math.max(0, Math.min(100, newScore));
    }
    
    private Integer calculateReputationScore(User user) {
        // Reputation based on community feedback and contribution
        Integer baseReputation = user.getReputationScore() != null ? user.getReputationScore() : 0;
        
        // This would be enhanced with actual community feedback data
        // For now, simple calculation based on account status
        Integer statusBonus = user.getIsActive() ? 10 : 0;
        
        return Math.max(0, baseReputation + statusBonus);
    }
    
    private Double calculateBalanceRatio(Integer freedomScore, Integer securityScore) {
        if (securityScore == 0) {
            return freedomScore > 0 ? Double.MAX_VALUE : 1.0;
        }
        return (double) freedomScore / securityScore;
    }
}