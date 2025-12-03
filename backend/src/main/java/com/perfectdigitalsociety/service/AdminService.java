package com.perfectdigitalsociety.service;

import com.perfectdigitalsociety.dto.request.AdjustBalanceRequest;
import com.perfectdigitalsociety.dto.request.UpdateStatusRequest;
import com.perfectdigitalsociety.dto.response.*;
import com.perfectdigitalsociety.entity.SystemMetric;
import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.exception.UnauthorizedException;
import com.perfectdigitalsociety.exception.UserNotFoundException;
import com.perfectdigitalsociety.mapper.UserMapper;
import com.perfectdigitalsociety.repository.BalanceEventRepository;
import com.perfectdigitalsociety.repository.MessageRepository;
import com.perfectdigitalsociety.repository.SystemMetricRepository;
import com.perfectdigitalsociety.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {
    
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BalanceEventRepository balanceEventRepository;
    private final SystemMetricRepository systemMetricRepository;
    private final UserMapper userMapper;
    private final BalanceService balanceService;
    private final CommunityService communityService;
    
    // Business Logic Methods as specified in documentation
    
    /**
     * Manage users (admin function)
     */
    public List<AdminUserResponse> manageUsers(Long adminId) {
        log.info("Admin ID: {} requesting user management data", adminId);
        
        validateAdminAccess(adminId);
        
        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
            .map(this::toAdminUserResponse)
            .toList();
    }
    
    /**
     * System overview (admin function)
     */
    public SystemMetricsResponse systemOverview(Long adminId) {
        log.info("Admin ID: {} requesting system overview", adminId);
        
        validateAdminAccess(adminId);
        
        // Gather system-wide metrics
        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countActiveUsers();
        Long totalMessages = messageRepository.count();
        Long totalBalanceEvents = balanceEventRepository.count();
        
        // Get current system balance
        SystemBalanceResponse systemBalance = balanceService.calculateSystemBalance();
        
        // Calculate average user balance
        Double avgFreedom = userRepository.getAverageFreedomScore();
        Double avgSecurity = userRepository.getAverageSecurityScore();
        Double averageUserBalance = (avgFreedom != null && avgSecurity != null) ? 
            (avgFreedom + avgSecurity) / 2 : 50.0;
        
        // Get community health
        CommunityMetricsResponse communityMetrics = communityService.analyzeMetrics();
        
        // Calculate system uptime (placeholder - would be from actual monitoring)
        Duration uptime = Duration.between(LocalDateTime.now().minusDays(30), LocalDateTime.now());
        
        // Get last system event
        LocalDateTime lastSystemEvent = balanceEventRepository.findMostRecentEvents(1)
            .stream()
            .findFirst()
            .map(event -> event.getCreatedAt())
            .orElse(LocalDateTime.now());
        
        SystemMetricsResponse response = new SystemMetricsResponse();
        response.setTotalUsers(totalUsers.intValue());
        response.setActiveUsers(activeUsers.intValue());
        response.setTotalMessages(totalMessages.intValue());
        response.setTotalBalanceEvents(totalBalanceEvents.intValue());
        response.setSystemFreedomLevel(systemBalance.getCurrentFreedomLevel());
        response.setSystemSecurityLevel(systemBalance.getCurrentSecurityLevel());
        response.setAverageUserBalance(averageUserBalance);
        response.setSystemHealth(communityMetrics.getCommunityHealth());
        response.setUptime(uptime.toMillis());
        response.setLastSystemEvent(lastSystemEvent);
        
        log.info("System overview generated for admin ID: {}", adminId);
        return response;
    }
    
    /**
     * Emergency actions (admin function)
     */
    public StatusResponse emergencyActions(Long adminId, String action, String reason) {
        log.info("Admin ID: {} triggering emergency action: {} with reason: {}", adminId, action, reason);

        validateAdminAccess(adminId);
        
        switch (action.toUpperCase()) {
            case "SYSTEM_LOCKDOWN" -> performSystemLockdown(reason);
            case "EMERGENCY_REBALANCE" -> performEmergencyRebalance(reason);
            case "MASS_MODERATION" -> performMassModeration(reason);
            case "RESET_SYSTEM_BALANCE" -> performSystemBalanceReset(reason);
            default -> throw new IllegalArgumentException("Unknown emergency action: " + action);
        }
        
        log.info("Emergency action completed: {}", action);
        return new StatusResponse(true, "Emergency action completed: " + action, LocalDateTime.now());
    }
    
    /**
     * Audit logs (admin function)
     */
    public List<BalanceEventResponse> auditLogs(Long adminId, int limit) {
        log.info("Admin ID: {} requesting audit logs with limit: {}", adminId, limit);
        
        validateAdminAccess(adminId);
        
        return balanceService.getBalanceHistory(limit);
    }
    
    /**
     * Update user status (admin function)
     */
    public StatusResponse updateUserStatus(Long adminId, Long userId, UpdateStatusRequest request) {
        log.info("Admin ID: {} updating status for user ID: {} to status: {}", adminId, userId, request.getStatus());
        
        validateAdminAccess(adminId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        switch (request.getStatus().toUpperCase()) {
            case "ACTIVE" -> {
                user.setIsActive(true);
                log.info("User ID: {} activated", userId);
            }
            case "INACTIVE" -> {
                user.setIsActive(false);
                log.info("User ID: {} deactivated", userId);
            }
            case "SUSPENDED" -> {
                user.setIsActive(false);
                // Additional suspension logic
                balanceService.adjustBalance(userId, -50, 50, "User suspended by admin: " + request.getReason());
                log.info("User ID: {} suspended", userId);
            }
            default -> throw new IllegalArgumentException("Unknown status: " + request.getStatus());
        }
        
        userRepository.save(user);
        
        return new StatusResponse(true, "User status updated successfully", LocalDateTime.now());
    }
    
    /**
     * Manual balance adjustment (admin function)
     */
    public BalanceEventResponse adjustUserBalance(Long adminId, AdjustBalanceRequest request) {
        log.info("Admin ID: {} adjusting balance for user ID: {}", adminId, request.getUserId());
        
        validateAdminAccess(adminId);
        
        return balanceService.adjustBalance(
            request.getUserId(),
            request.getFreedomAdjustment(),
            request.getSecurityAdjustment(),
            "Admin adjustment: " + request.getReason()
        );
    }
    
    // Additional admin service methods
    
    public List<AdminUserResponse> getSuspiciousUsers(Long adminId) {
        log.info("Admin ID: {} requesting suspicious users", adminId);
        
        validateAdminAccess(adminId);
        
        return userRepository.findAll()
            .stream()
            .filter(this::isSuspiciousUser)
            .map(this::toAdminUserResponse)
            .toList();
    }
    
    public void performSystemMaintenance(Long adminId) {
        log.info("Admin ID: {} performing system maintenance", adminId);

        validateAdminAccess(adminId);
        
        // Cleanup old metrics
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<SystemMetric> oldMetrics =
            systemMetricRepository.findMetricsSince(thirtyDaysAgo);
        
        // Evaluate rule effectiveness
        communityService.evaluateRuleEffectiveness();
        
        // Perform automatic rebalancing
        balanceService.performAutomaticRebalancing();
        
        log.info("System maintenance completed");
    }
    
    public String generateSystemReport(Long adminId) {
        log.info("Admin ID: {} generating system report", adminId);
        
        validateAdminAccess(adminId);
        
        SystemMetricsResponse metrics = systemOverview(adminId);
        String communityReport = communityService.generateReports();
        
        StringBuilder report = new StringBuilder();
        report.append("=== SYSTEM ADMINISTRATION REPORT ===\n");
        report.append("Generated at: ").append(LocalDateTime.now()).append("\n");
        report.append("Generated by Admin ID: ").append(adminId).append("\n\n");
        
        report.append("SYSTEM METRICS:\n");
        report.append("- Total Users: ").append(metrics.getTotalUsers()).append("\n");
        report.append("- Active Users: ").append(metrics.getActiveUsers()).append("\n");
        report.append("- Total Messages: ").append(metrics.getTotalMessages()).append("\n");
        report.append("- Total Balance Events: ").append(metrics.getTotalBalanceEvents()).append("\n");
        report.append("- System Freedom Level: ").append(metrics.getSystemFreedomLevel()).append("\n");
        report.append("- System Security Level: ").append(metrics.getSystemSecurityLevel()).append("\n");
        report.append("- System Health: ").append(String.format("%.2f", metrics.getSystemHealth())).append("\n");
        report.append("- System Uptime: ").append(metrics.getUptime() / 1000 / 60 / 60).append(" hours\n\n");
        
        report.append(communityReport);

        return report.toString();
    }
    
    // Private helper methods
    
    private void validateAdminAccess(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        if (user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("User does not have admin privileges");
        }
        
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Admin account is not active");
        }
    }
    
    private AdminUserResponse toAdminUserResponse(User user) {
        Long messageCount = messageRepository.countMessagesByUser(user.getId());
        Integer flagCount = user.getMessages() != null ? 
            user.getMessages().stream().mapToInt(message -> message.getFlagCount()).sum() : 0;
        
        AdminUserResponse response = new AdminUserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole().toString());
        response.setFreedomScore(user.getFreedomScore());
        response.setSecurityScore(user.getSecurityScore());
        response.setReputationScore(user.getReputationScore());
        response.setIsActive(user.getIsActive());
        response.setMessageCount(messageCount.intValue());
        response.setFlagCount(flagCount);
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLoginAt(user.getLastLoginAt());
        
        return response;
    }
    
    public AdminUserResponse convertToAdminUserResponse(User user) {
        return toAdminUserResponse(user);
    }

    private boolean isSuspiciousUser(User user) {
        // Define criteria for suspicious users
        if (!user.getIsActive()) return false;

        Long messageCount = messageRepository.countMessagesByUser(user.getId());
        Integer totalFlags = user.getMessages() != null ? 
            user.getMessages().stream().mapToInt(message -> message.getFlagCount()).sum() : 0;
        
        // Suspicious if high flag rate or extreme scores
        return (messageCount > 0 && totalFlags > messageCount * 0.5) || 
               user.getFreedomScore() < 10 || 
               user.getSecurityScore() < 10 ||
               user.getReputationScore() < -50;
    }
    
    private void performSystemLockdown(String reason) {
        log.warn("SYSTEM LOCKDOWN initiated: {}", reason);
        
        // Deactivate all non-admin users temporarily
        List<User> nonAdminUsers = userRepository.findByRole(User.Role.USER);
        nonAdminUsers.addAll(userRepository.findByRole(User.Role.MODERATOR));

        for (User user : nonAdminUsers) {
            user.setIsActive(false);
        }
        
        userRepository.saveAll(nonAdminUsers);

        // Log the emergency action
        balanceService.adjustBalance(1L, 0, 0, "EMERGENCY: System lockdown - " + reason);
    }
    
    private void performEmergencyRebalance(String reason) {
        log.warn("EMERGENCY REBALANCE initiated: {}", reason);
        
        // Reset all user scores to default
        List<User> allUsers = userRepository.findAllActiveUsers();
        for (User user : allUsers) {
            user.setFreedomScore(50);
            user.setSecurityScore(50);
        }
        
        userRepository.saveAll(allUsers);

        // Trigger system rebalancing
        balanceService.performAutomaticRebalancing();
    }
    
    private void performMassModeration(String reason) {
        log.warn("MASS MODERATION initiated: {}", reason);
        
        // Hide all flagged messages
        messageRepository.findAllFlaggedMessages()
            .forEach(message -> {
                message.setIsVisible(false);
                message.setModerationStatus(com.perfectdigitalsociety.entity.Message.ModerationStatus.REJECTED);
            });
    }
    
    private void performSystemBalanceReset(String reason) {
        log.warn("SYSTEM BALANCE RESET initiated: {}", reason);
        
        // Reset system to default balanced state
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            user.setFreedomScore(50);
            user.setSecurityScore(50);
            user.setReputationScore(0);
        }
        
        userRepository.saveAll(allUsers);
    }

    public User getUserById(Long adminId, Long userId) {
        log.info("Admin ID: {} getting user by ID: {}", adminId, userId);

        validateAdminAccess(adminId);

        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    public List<User> searchUsers(Long adminId, String username, String email, String role) {
        log.info("Admin ID: {} searching users - username: {}, email: {}, role: {}", adminId, username, email, role);

        validateAdminAccess(adminId);

        if (username != null && !username.trim().isEmpty()) {
            return userRepository.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(username.trim().toLowerCase()))
                .toList();
        }

        if (email != null && !email.trim().isEmpty()) {
            return userRepository.findAll().stream()
                .filter(user -> user.getEmail().toLowerCase().contains(email.trim().toLowerCase()))
                .toList();
        }

        if (role != null && !role.trim().isEmpty()) {
            try {
                User.Role userRole = User.Role.valueOf(role.toUpperCase());
                return userRepository.findByRole(userRole);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid role specified: {}", role);
                return List.of();
            }
        }

        // If no specific criteria, return all active users
        return userRepository.findAllActiveUsers();
    }
}


