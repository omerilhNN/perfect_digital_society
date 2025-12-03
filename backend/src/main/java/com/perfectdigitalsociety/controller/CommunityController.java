package com.perfectdigitalsociety.controller;

import com.perfectdigitalsociety.dto.request.CreateRuleRequest;
import com.perfectdigitalsociety.dto.request.VoteRequest;
import com.perfectdigitalsociety.dto.response.CommunityMetricsResponse;
import com.perfectdigitalsociety.dto.response.CommunityRuleResponse;
import com.perfectdigitalsociety.dto.response.VoteResponse;
import com.perfectdigitalsociety.entity.CommunityRule;
import com.perfectdigitalsociety.service.CommunityService;
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
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommunityController {
    
    private final CommunityService communityService;
    private final UserService userService;
    
    /**
     * Get community rules
     * GET /api/community/rules
     */
    @GetMapping("/rules")
    public ResponseEntity<List<CommunityRuleResponse>> getCommunityRules(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean activeOnly) {
        
        log.info("Get community rules request - type: {}, activeOnly: {}", type, activeOnly);
        
        try {
            List<CommunityRuleResponse> rules;
            
            if (type != null && ! type.isEmpty()) {
                // Filter by rule type
                CommunityRule.RuleType ruleType = CommunityRule.RuleType.valueOf(type.toUpperCase());
                rules = communityService.getRulesByType(ruleType);
                log.info("Retrieved {} rules of type: {}", rules.size(), type);
            } else if (activeOnly != null && activeOnly) {
                // Get only active rules
                rules = communityService.getActiveRules();
                log.info("Retrieved {} active rules", rules.size());
            } else {
                // Get all active rules (default)
                rules = communityService.getActiveRules();
                log.info("Retrieved {} rules", rules.size());
            }
            
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            log.error("Failed to get community rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create new rule
     * POST /api/community/rules
     */
    @PostMapping("/rules")
    public ResponseEntity<CommunityRuleResponse> createRule(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateRuleRequest request) {
        
        log.info("Create rule request from user: {} - title: {}", userDetails.getUsername(), request.getTitle());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            CommunityRuleResponse response = communityService.createRule(userId, request);
            
            log.info("Community rule created successfully with ID: {} by user: {}", 
                    response.getId(), userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to create rule for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Vote on rule
     * POST /api/community/rules/{id}/vote
     */
    @PostMapping("/rules/{id}/vote")
    public ResponseEntity<VoteResponse> voteOnRule(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VoteRequest request) {
        
        log.info("Vote on rule request for ID: {} from user: {} with vote: {}", 
                id, userDetails.getUsername(), request.getVote());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            
            // Set the rule ID from path parameter
            request.setRuleId(id);
            
            VoteResponse response = communityService.voteOnRule(userId, request);
            
            log.info("Vote recorded successfully for rule ID: {} by user: {}", 
                    id, userDetails.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to vote on rule ID: {} for user: {}", id, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get community metrics
     * GET /api/community/metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<CommunityMetricsResponse> getCommunityMetrics() {
        log.info("Get community metrics request");
        
        try {
            CommunityMetricsResponse response = communityService.analyzeMetrics();
            
            log.info("Community metrics retrieved - Health: {}, Total Users: {}, Active Users: {}", 
                    response.getCommunityHealth(), response.getTotalUsers(), response.getActiveUsers());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get community metrics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get user's created rules
     * GET /api/community/my-rules
     */
    @GetMapping("/my-rules")
    public ResponseEntity<List<CommunityRuleResponse>> getUserCreatedRules(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get user created rules request for: {}", userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            List<CommunityRuleResponse> rules = communityService.getUserCreatedRules(userId);
            
            log.info("Retrieved {} rules created by user: {}", rules.size(), userDetails.getUsername());
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            log.error("Failed to get user created rules for: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get specific rule by ID
     * GET /api/community/rules/{id}
     */
    @GetMapping("/rules/{id}")
    public ResponseEntity<CommunityRuleResponse> getRuleById(@PathVariable Long id) {
        log.info("Get rule by ID request for: {}", id);
        
        try {
            CommunityRuleResponse response = communityService.getRuleById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get rule with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * Get rules by type
     * GET /api/community/rules/type/{type}
     */
    @GetMapping("/rules/type/{type}")
    public ResponseEntity<List<CommunityRuleResponse>> getRulesByType(@PathVariable String type) {
        log.info("Get rules by type request for: {}", type);
        
        try {
            CommunityRule.RuleType ruleType = CommunityRule.RuleType.valueOf(type.toUpperCase());
            List<CommunityRuleResponse> rules = communityService.getRulesByType(ruleType);
            
            log.info("Retrieved {} rules of type: {}", rules.size(), type);
            return ResponseEntity.ok(rules);
        } catch (IllegalArgumentException e) {
            log.error("Invalid rule type: {}", type);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Failed to get rules by type: {}", type, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get community health report (detailed)
     * GET /api/community/health-report
     */
    @GetMapping("/health-report")
    public ResponseEntity<String> getCommunityHealthReport(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Get community health report request from user: {}", userDetails.getUsername());
        
        try {
            // Check if user has appropriate rights to view detailed reports
            Long userId = getCurrentUserId(userDetails.getUsername());
            if (!hasReportViewingRights(userId)) {
                log.warn("User {} attempted to access health report without proper rights", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            String report = communityService.generateReports();
            
            log.info("Community health report generated for user: {}", userDetails.getUsername());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Failed to generate community health report for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get top voted rules
     * GET /api/community/rules/top-voted
     */
    @GetMapping("/rules/top-voted")
    public ResponseEntity<List<CommunityRuleResponse>> getTopVotedRules(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Get top voted rules request with limit: {}", limit);
        
        try {
            List<CommunityRuleResponse> rules = communityService.getTopVotedRules(limit);
            log.info("Retrieved {} top voted rules", rules.size());
            return ResponseEntity.ok(rules);
        } catch (Exception e) {
            log.error("Failed to get top voted rules", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get recent community activity
     * GET /api/community/recent-activity
     */
    @GetMapping("/recent-activity")
    public ResponseEntity<List<CommunityRuleResponse>> getRecentCommunityActivity(
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("Get recent community activity request with limit: {}", limit);
        
        try {
            List<CommunityRuleResponse> recentRules = communityService.getRecentRules(limit);
            log.info("Retrieved {} recent community activities", recentRules.size());
            return ResponseEntity.ok(recentRules);
        } catch (Exception e) {
            log.error("Failed to get recent community activity", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Evaluate rule effectiveness (admin/moderator only)
     * POST /api/community/evaluate-rules
     */
    @PostMapping("/evaluate-rules")
    public ResponseEntity<String> evaluateRuleEffectiveness(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Evaluate rule effectiveness request from user: {}", userDetails.getUsername());
        
        try {
            Long userId = getCurrentUserId(userDetails.getUsername());
            if (!hasModeratorRights(userId)) {
                log.warn("User {} attempted unauthorized rule evaluation", userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            communityService.evaluateRuleEffectiveness();
            
            log.info("Rule effectiveness evaluation completed by user: {}", userDetails.getUsername());
            return ResponseEntity.ok("Rule effectiveness evaluation completed successfully");
            
        } catch (Exception e) {
            log.error("Failed to evaluate rule effectiveness for user: {}", userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Helper methods
    private Long getCurrentUserId(String username) {
        return userService.getUserByUsername(username).getId();
    }
    
    private boolean hasReportViewingRights(Long userId) {
        // Users with moderator or admin rights can view detailed reports
        return userService.hasModeratorRights(userId) || userService.hasAdminRights(userId);
    }
    
    private boolean hasModeratorRights(Long userId) {
        return userService.hasModeratorRights(userId) || userService.hasAdminRights(userId);
    }
}