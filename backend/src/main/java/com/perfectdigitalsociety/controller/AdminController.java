package com.perfectdigitalsociety.controller;

import com.perfectdigitalsociety.dto.response.AdminUserResponse;
import com.perfectdigitalsociety.dto.response.BalanceEventResponse;
import com.perfectdigitalsociety.dto.response.StatusResponse;
import com.perfectdigitalsociety.dto.response.SystemMetricsResponse;
import com.perfectdigitalsociety.dto.request.AdjustBalanceRequest;
import com.perfectdigitalsociety.dto.request.UpdateStatusRequest;
import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.service.AdminService;
import com.perfectdigitalsociety.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Admin Management", description = "Yönetici paneli endpoint'leri - sistem yönetimi, kullanıcı moderasyonu ve sistem metrikleri")
@SecurityRequirement(name = "bearer-jwt")
public class AdminController {
    
    private final AdminService adminService;
    private final UserService userService;
    
    /**
     * Get all users (admin only)
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get all users request from admin: {}", userDetails.getUsername());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized access to admin users", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<AdminUserResponse> users = adminService.manageUsers(adminId);
            log.info("Retrieved {} users for admin: {}", users.size(), userDetails.getUsername());
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            log.error("Failed to get all users for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update user status (admin only)
     * PUT /api/admin/users/{id}/status
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<StatusResponse> updateUserStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateStatusRequest request) {
        
        log.info("Update user status request for user ID: {} from admin: {} to status: {}", 
                id, userDetails.getUsername(), request.getStatus());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized user status update", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            StatusResponse response = adminService.updateUserStatus(adminId, id, request);
            
            log.info("User status updated successfully for user ID: {} by admin: {}", 
                    id, userDetails.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update user status for ID: {} by admin: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get system-wide metrics (admin only)
     * GET /api/admin/system/metrics
     */
    @GetMapping("/system/metrics")
    public ResponseEntity<SystemMetricsResponse> getSystemMetrics(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get system metrics request from admin: {}", userDetails.getUsername());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized access to system metrics", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            SystemMetricsResponse response = adminService.systemOverview(adminId);
            
            log.info("System metrics retrieved for admin: {} - Total Users: {}, System Health: {}", 
                    userDetails.getUsername(), response.getTotalUsers(), response.getSystemHealth());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get system metrics for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Manual balance adjustment (admin only)
     * POST /api/admin/balance/adjust
     */
    @PostMapping("/balance/adjust")
    public ResponseEntity<BalanceEventResponse> adjustBalance(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AdjustBalanceRequest request) {
        
        log.info("Manual balance adjustment request from admin: {} for user ID: {}", 
                userDetails.getUsername(), request.getUserId());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized balance adjustment", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            BalanceEventResponse response = adminService.adjustUserBalance(adminId, request);
            
            log.info("Balance adjustment completed for user ID: {} by admin: {} - Freedom: {}, Security: {}", 
                    request.getUserId(), userDetails.getUsername(), 
                    request.getFreedomAdjustment(), request.getSecurityAdjustment());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to adjust balance for user ID: {} by admin: {}", 
                    request.getUserId(), userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get audit logs (admin only)
     * GET /api/admin/audit-logs
     */
    @GetMapping("/audit-logs")
    public ResponseEntity<List<BalanceEventResponse>> getAuditLogs(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "100") int limit) {
        
        log.info("Get audit logs request from admin: {} with limit: {}", userDetails.getUsername(), limit);
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized access to audit logs", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<BalanceEventResponse> auditLogs = adminService.auditLogs(adminId, limit);
            
            log.info("Retrieved {} audit log entries for admin: {}", auditLogs.size(), userDetails.getUsername());
            return ResponseEntity.ok(auditLogs);
            
        } catch (Exception e) {
            log.error("Failed to get audit logs for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get suspicious users (admin only)
     * GET /api/admin/users/suspicious
     */
    @GetMapping("/users/suspicious")
    public ResponseEntity<List<AdminUserResponse>> getSuspiciousUsers(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get suspicious users request from admin: {}", userDetails.getUsername());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized access to suspicious users", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<AdminUserResponse> suspiciousUsers = adminService.getSuspiciousUsers(adminId);
            
            log.info("Retrieved {} suspicious users for admin: {}", suspiciousUsers.size(), userDetails.getUsername());
            return ResponseEntity.ok(suspiciousUsers);
            
        } catch (Exception e) {
            log.error("Failed to get suspicious users for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Perform emergency actions (admin only)
     * POST /api/admin/emergency
     */
    @PostMapping("/emergency")
    public ResponseEntity<StatusResponse> performEmergencyAction(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String action,
            @RequestParam String reason) {
        
        log.warn("Emergency action request from admin: {} - Action: {}, Reason: {}", 
                userDetails.getUsername(), action, reason);
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (! hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized emergency action", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            StatusResponse response = adminService.emergencyActions(adminId, action, reason);
            
            log.warn("Emergency action completed by admin: {} - Action: {}", userDetails.getUsername(), action);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to perform emergency action for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Generate system report (admin only)
     * GET /api/admin/system/report
     */
    @GetMapping("/system/report")
    public ResponseEntity<String> generateSystemReport(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Generate system report request from admin: {}", userDetails.getUsername());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized system report generation", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            String report = adminService.generateSystemReport(adminId);
            
            log.info("System report generated successfully for admin: {}", userDetails.getUsername());
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            log.error("Failed to generate system report for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Perform system maintenance (admin only)
     * POST /api/admin/system/maintenance
     */
    @PostMapping("/system/maintenance")
    public ResponseEntity<StatusResponse> performSystemMaintenance(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("System maintenance request from admin: {}", userDetails.getUsername());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized system maintenance", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            adminService.performSystemMaintenance(adminId);
            
            log.info("System maintenance completed successfully by admin: {}", userDetails.getUsername());
            return ResponseEntity.ok(new StatusResponse(true, "System maintenance completed successfully", java.time.LocalDateTime.now()));
            
        } catch (Exception e) {
            log.error("Failed to perform system maintenance for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get user details by ID (admin only)
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Get user by ID request for user ID: {} from admin: {}", id, userDetails.getUsername());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized access to user details", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            User user = adminService.getUserById(adminId, id);
            AdminUserResponse userResponse = adminService.convertToAdminUserResponse(user);
            return ResponseEntity.ok(userResponse);

        } catch (Exception e) {
            log.error("Failed to get user by ID: {} for admin: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Search users (admin only)
     * GET /api/admin/users/search
     */
    @GetMapping("/users/search")
    public ResponseEntity<List<AdminUserResponse>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Search users request from admin: {} - username: {}, email: {}, role: {}", 
                userDetails.getUsername(), username, email, role);
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized user search", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<User> users = adminService.searchUsers(adminId, username, email, role);
            List<AdminUserResponse> userResponses = users.stream()
                .map(adminService::convertToAdminUserResponse)
                .toList();

            log.info("Found {} users matching search criteria for admin: {}", userResponses.size(), userDetails.getUsername());
            return ResponseEntity.ok(userResponses);

        } catch (Exception e) {
            log.error("Failed to search users for admin: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get system statistics dashboard (admin only)
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<SystemMetricsResponse> getAdminDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get admin dashboard request from: {}", userDetails.getUsername());
        
        try {
            Long adminId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(adminId)) {
                log.warn("User {} attempted unauthorized access to admin dashboard", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            SystemMetricsResponse dashboard = adminService.systemOverview(adminId);
            
            log.info("Admin dashboard data retrieved for: {}", userDetails.getUsername());
            return ResponseEntity.ok(dashboard);
            
        } catch (Exception e) {
            log.error("Failed to get admin dashboard for: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Helper methods
    private Long getCurrentUserId(String username) {
        return userService.getUserByUsername(username).getId();
    }
    
    private boolean hasAdminRights(Long userId) {
        return userService.hasAdminRights(userId);
    }
}