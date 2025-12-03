package com.perfectdigitalsociety.controller;

import com.perfectdigitalsociety.dto.request.TriggerBalanceRequest;
import com.perfectdigitalsociety.dto.response.BalanceEventResponse;
import com.perfectdigitalsociety.dto.response.SystemBalanceResponse;
import com.perfectdigitalsociety.dto.response.UserBalanceResponse;
import com.perfectdigitalsociety.service.BalanceService;
import com.perfectdigitalsociety.service.UserService;
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
@RequestMapping("/api/balance")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class BalanceController {
    
    private final BalanceService balanceService;
    private final UserService userService;
    
    /**
     * Get current system balance
     * GET /api/balance/current
     */
    @GetMapping("/current")
    public ResponseEntity<SystemBalanceResponse> getCurrentSystemBalance() {
        log.info("Get current system balance request");
        
        try {
            SystemBalanceResponse response = balanceService.calculateSystemBalance();
            log.info("System balance retrieved - Freedom: {}, Security: {}, Balance Score: {}", 
                    response.getCurrentFreedomLevel(), 
                    response.getCurrentSecurityLevel(), 
                    response.getBalanceScore());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get current system balance", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get balance event history
     * GET /api/balance/events
     */
    @GetMapping("/events")
    public ResponseEntity<List<BalanceEventResponse>> getBalanceEventHistory(
            @RequestParam(defaultValue = "50") int limit) {
        
        log.info("Get balance event history request with limit: {}", limit);
        
        try {
            List<BalanceEventResponse> events = balanceService.getBalanceHistory(limit);
            log.info("Retrieved {} balance events", events.size());
            return ResponseEntity.ok(events);
        } catch (Exception e) {
            log.error("Failed to get balance event history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Manual balance trigger
     * POST /api/balance/trigger
     */
    @PostMapping("/trigger")
    public ResponseEntity<BalanceEventResponse> triggerBalance(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TriggerBalanceRequest request) {
        
        log.info("Manual balance trigger request from user: {} with event type: {}", 
                userDetails.getUsername(), request.getEventType());
        
        try {
            // Check if user has admin/moderator rights for manual triggers
            Long userId = getCurrentUserId(userDetails.getUsername());
            if (!hasBalanceManagementRights(userId)) {
                log.warn("User {} attempted unauthorized balance trigger", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            BalanceEventResponse response = balanceService.triggerBalanceEvent(userId, request);
            
            log.info("Balance event triggered successfully by user: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to trigger balance event for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get user-specific balance
     * GET /api/balance/user/{id}
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<UserBalanceResponse> getUserBalance(@PathVariable Long id) {
        log.info("Get user balance request for ID: {}", id);
        
        try {
            UserBalanceResponse response = balanceService.getUserBalance(id);
            log.info("User balance retrieved for ID: {} - Freedom: {}, Security: {}", 
                    id, response.getFreedomScore(), response.getSecurityScore());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get user balance for ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Get current user's balance
     * GET /api/balance/my-balance
     */
    @GetMapping("/my-balance")
    public ResponseEntity<UserBalanceResponse> getCurrentUserBalance(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get current user balance request for: {}", userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            UserBalanceResponse response = balanceService.getUserBalance(userId);
            
            log.info("Current user balance retrieved for: {} - Freedom: {}, Security: {}", 
                    userDetails.getUsername(), response.getFreedomScore(), response.getSecurityScore());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get current user balance for: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Get balance trends (additional endpoint for analytics)
     * GET /api/balance/trends
     */
    @GetMapping("/trends")
    public ResponseEntity<List<BalanceEventResponse>> getBalanceTrends(
            @RequestParam(defaultValue = "24") int hours) {
        
        log.info("Get balance trends request for last {} hours", hours);
        
        try {
            // This would implement trend analysis over time
            List<BalanceEventResponse> recentEvents = balanceService.getBalanceHistory(100);
            
            // Filter events from last X hours
            List<BalanceEventResponse> trendEvents = recentEvents.stream()
                .filter(event -> event.getCreatedAt().isAfter(
                    java.time.LocalDateTime.now().minusHours(hours)))
                .toList();
            
            log.info("Retrieved {} trend events for last {} hours", trendEvents.size(), hours);
            return ResponseEntity.ok(trendEvents);
        } catch (Exception e) {
            log.error("Failed to get balance trends for {} hours", hours, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get system balance statistics
     * GET /api/balance/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<SystemBalanceResponse> getBalanceStatistics() {
        log.info("Get balance statistics request");
        
        try {
            SystemBalanceResponse response = balanceService.calculateSystemBalance();
            
            // Add additional statistics calculations here if needed
            log.info("Balance statistics retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get balance statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Trigger automatic rebalancing (admin only)
     * POST /api/balance/rebalance
     */
    @PostMapping("/rebalance")
    public ResponseEntity<BalanceEventResponse> triggerAutomaticRebalancing(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Automatic rebalancing request from user: {}", userDetails.getUsername());
        
        try {
            // Check admin rights
            Long userId = getCurrentUserId(userDetails.getUsername());
            if (!hasAdminRights(userId)) {
                log.warn("User {} attempted unauthorized automatic rebalancing", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Trigger automatic rebalancing
            balanceService.performAutomaticRebalancing();
            
            // Return latest balance event
            List<BalanceEventResponse> events = balanceService.getBalanceHistory(1);
            BalanceEventResponse latestEvent = events.isEmpty() ?  null : events.get(0);
            
            log.info("Automatic rebalancing triggered successfully by admin: {}", userDetails.getUsername());
            return ResponseEntity.ok(latestEvent);
            
        } catch (Exception e) {
            log.error("Failed to trigger automatic rebalancing for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Helper methods
    private Long getCurrentUserId(String username) {
        return userService.getUserByUsername(username).getId();
    }
    
    private boolean hasBalanceManagementRights(Long userId) {
        return userService.hasModeratorRights(userId) || userService.hasAdminRights(userId);
    }
    
    private boolean hasAdminRights(Long userId) {
        return userService.hasAdminRights(userId);
    }
}