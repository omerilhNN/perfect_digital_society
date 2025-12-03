package com.perfectdigitalsociety.service;

import com.perfectdigitalsociety.dto.request.CreateRuleRequest;
import com.perfectdigitalsociety.dto.request.VoteRequest;
import com.perfectdigitalsociety.dto.response.CommunityMetricsResponse;
import com.perfectdigitalsociety.dto.response.CommunityRuleResponse;
import com.perfectdigitalsociety.dto.response.VoteResponse;
import com.perfectdigitalsociety.entity.CommunityRule;
import com.perfectdigitalsociety.entity.SystemMetric;
import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.exception.RuleNotFoundException;
import com.perfectdigitalsociety.exception.UserNotFoundException;
import com.perfectdigitalsociety.mapper.CommunityMapper;
import com.perfectdigitalsociety.repository.CommunityRuleRepository;
import com.perfectdigitalsociety.repository.MessageRepository;
import com.perfectdigitalsociety.repository.SystemMetricRepository;
import com.perfectdigitalsociety.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommunityService {
    
    private final CommunityRuleRepository communityRuleRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final SystemMetricRepository systemMetricRepository;
    private final CommunityMapper communityMapper;
    
    // Business Logic Methods as specified in documentation
    
    /**
     * Create new community rule
     */
    public CommunityRuleResponse createRule(Long userId, CreateRuleRequest request) {
        log.info("Creating community rule by user ID: {}", userId);
        
        User creator = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        // Create community rule
        CommunityRule rule = new CommunityRule();
        rule.setTitle(request.getTitle());
        rule.setDescription(request.getDescription());
        rule.setRuleType(CommunityRule.RuleType.valueOf(request.getRuleType().toUpperCase()));
        rule.setPriority(request.getPriority());
        rule.setThreshold(request.getThreshold());
        rule.setAction(CommunityRule.Action.valueOf(request.getAction().toUpperCase()));
        rule.setIsActive(false); // Requires community approval
        rule.setVotes(1); // Creator's initial vote
        rule.setCreatedBy(creator);
        
        CommunityRule savedRule = communityRuleRepository.save(rule);
        
        // Update creator's reputation
        creator.setReputationScore(creator.getReputationScore() + 5);
        userRepository.save(creator);
        
        log.info("Community rule created successfully with ID: {}", savedRule.getId());
        return communityMapper.toCommunityRuleResponse(savedRule);
    }
    
    /**
     * Vote on community rule
     */
    public VoteResponse voteOnRule(Long userId, VoteRequest request) {
        log.info("User ID: {} voting on rule ID: {} with vote: {}", userId, request.getRuleId(), request.getVote());
        
        User voter = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        CommunityRule rule = communityRuleRepository.findById(request.getRuleId())
            .orElseThrow(() -> new RuleNotFoundException("Rule not found with ID: " + request.getRuleId()));
        
        // Update vote count
        if (request.getVote()) {
            rule.setVotes(rule.getVotes() + 1);
        } else {
            rule.setVotes(rule.getVotes() - 1);
        }
        
        // Check if rule should be activated based on votes
        Integer activationThreshold = calculateActivationThreshold();
        if (rule.getVotes() >= activationThreshold && ! rule.getIsActive()) {
            rule.setIsActive(true);
            log.info("Rule ID: {} activated with {} votes", rule.getId(), rule.getVotes());
        } else if (rule.getVotes() < 0 && rule.getIsActive()) {
            rule.setIsActive(false);
            log.info("Rule ID: {} deactivated due to negative votes", rule.getId());
        }
        
        communityRuleRepository.save(rule);
        
        // Update voter's reputation
        voter.setReputationScore(voter.getReputationScore() + 1);
        userRepository.save(voter);
        
        // Calculate vote statistics
        VoteResponse response = new VoteResponse();
        response.setRuleId(request.getRuleId());
        response.setTotalVotes(Math.abs(rule.getVotes()));
        response.setPositiveVotes(Math.max(0, rule.getVotes()));
        response.setNegativeVotes(Math.max(0, -rule.getVotes()));
        response.setUserVote(request.getVote());
        
        log.info("Vote recorded successfully for rule ID: {}", request.getRuleId());
        return response;
    }
    
    /**
     * Analyze community metrics
     */
    public CommunityMetricsResponse analyzeMetrics() {
        log.info("Analyzing community metrics");
        
        // Gather community statistics
        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countActiveUsers();
        Long totalMessages = messageRepository.count();
        Long flaggedMessages = messageRepository.countFlaggedMessages();
        
        Double averageFreedomScore = userRepository.getAverageFreedomScore();
        Double averageSecurityScore = userRepository.getAverageSecurityScore();
        Double averageReputationScore = userRepository.getAverageReputationScore();
        
        // Calculate community health score
        Double communityHealth = calculateCommunityHealth(activeUsers, totalUsers, flaggedMessages, totalMessages);
        
        // Save metrics
        saveMetric("total_users", BigDecimal.valueOf(totalUsers));
        saveMetric("active_users", BigDecimal.valueOf(activeUsers));
        saveMetric("community_health", BigDecimal.valueOf(communityHealth));
        saveMetric("avg_freedom_score", BigDecimal.valueOf(averageFreedomScore != null ? averageFreedomScore : 0));
        saveMetric("avg_security_score", BigDecimal.valueOf(averageSecurityScore != null ? averageSecurityScore : 0));
        
        CommunityMetricsResponse response = new CommunityMetricsResponse();
        response.setTotalUsers(totalUsers.intValue());
        response.setActiveUsers(activeUsers.intValue());
        response.setTotalMessages(totalMessages.intValue());
        response.setFlaggedMessages(flaggedMessages.intValue());
        response.setAverageFreedomScore(averageFreedomScore != null ? averageFreedomScore : 0.0);
        response.setAverageSecurityScore(averageSecurityScore != null ? averageSecurityScore : 0.0);
        response.setCommunityHealth(communityHealth);
        response.setLastCalculated(LocalDateTime.now());
        
        log.info("Community metrics analyzed - Health Score: {}, Active Users: {}/{}", 
                communityHealth, activeUsers, totalUsers);
        
        return response;
    }
    
    /**
     * Generate community reports
     */
    public String generateReports() {
        log.info("Generating community reports");
        
        CommunityMetricsResponse metrics = analyzeMetrics();
        List<CommunityRule> activeRules = communityRuleRepository.findAllActiveRulesOrderedByPriority();
        List<CommunityRule> topVotedRules = communityRuleRepository.findTopVotedRules(10);
        
        StringBuilder report = new StringBuilder();
        report.append("=== COMMUNITY HEALTH REPORT ===\n");
        report.append("Generated at: ").append(LocalDateTime.now()).append("\n\n");
        
        report.append("COMMUNITY METRICS:\n");
        report.append("- Total Users: ").append(metrics.getTotalUsers()).append("\n");
        report.append("- Active Users: ").append(metrics.getActiveUsers()).append("\n");
        report.append("- Activity Rate: ").append(String.format("%.2f%%", 
            (metrics.getActiveUsers().doubleValue() / metrics.getTotalUsers() * 100))).append("\n");
        report.append("- Community Health: ").append(String.format("%.2f", metrics.getCommunityHealth())).append("\n");
        report.append("- Average Freedom Score: ").append(String.format("%.1f", metrics.getAverageFreedomScore())).append("\n");
        report.append("- Average Security Score: ").append(String.format("%.1f", metrics.getAverageSecurityScore())).append("\n\n");
        
        report.append("CONTENT MODERATION:\n");
        report.append("- Total Messages: ").append(metrics.getTotalMessages()).append("\n");
        report.append("- Flagged Messages: ").append(metrics.getFlaggedMessages()).append("\n");
        report.append("- Flag Rate: ").append(String.format("%.2f%%", 
            (metrics.getFlaggedMessages().doubleValue() / metrics.getTotalMessages() * 100))).append("\n\n");
        
        report.append("ACTIVE COMMUNITY RULES (").append(activeRules.size()).append("):\n");
        for (CommunityRule rule : activeRules) {
            report.append("- ").append(rule.getTitle())
                .append(" (Priority: ").append(rule.getPriority())
                .append(", Votes: ").append(rule.getVotes()).append(")\n");
        }
        
        String reportContent = report.toString();
        log.info("Community report generated successfully");
        
        return reportContent;
    }
    
    // Additional service methods
    
    public List<CommunityRuleResponse> getActiveRules() {
        log.info("Getting active community rules");
        return communityRuleRepository.findAllActiveRulesOrderedByPriority()
            .stream()
            .map(communityMapper::toCommunityRuleResponse)
            .toList();
    }
    
    public List<CommunityRuleResponse> getRulesByType(CommunityRule.RuleType type) {
        log.info("Getting rules by type: {}", type);
        return communityRuleRepository.findActiveRulesByType(type)
            .stream()
            .map(communityMapper::toCommunityRuleResponse)
            .toList();
    }
    
    public Integer getActiveFlagThreshold() {
        // Get the lowest threshold from active SECURITY rules
        return communityRuleRepository.findActiveRulesByType(CommunityRule.RuleType.SECURITY)
            .stream()
            .mapToInt(CommunityRule::getThreshold)
            .min()
            .orElse(5); // Default threshold
    }
    
    public List<CommunityRuleResponse> getUserCreatedRules(Long userId) {
        log.info("Getting rules created by user ID: {}", userId);
        return communityRuleRepository.findByCreatedByUserId(userId)
            .stream()
            .map(communityMapper::toCommunityRuleResponse)
            .toList();
    }
    
    public void evaluateRuleEffectiveness() {
        log.info("Evaluating rule effectiveness");
        
        List<CommunityRule> activeRules = communityRuleRepository.findAllActiveRulesOrderedByPriority();
        
        for (CommunityRule rule : activeRules) {
            // Simple effectiveness evaluation based on community feedback
            if (rule.getVotes() < -10) {
                rule.setIsActive(false);
                log.info("Deactivating ineffective rule ID: {} due to negative community feedback", rule.getId());
            }
        }
        
        communityRuleRepository.saveAll(activeRules);
    }
    
    public CommunityRuleResponse getRuleById(Long ruleId) {
        log.info("Getting rule by ID: {}", ruleId);

        CommunityRule rule = communityRuleRepository.findById(ruleId)
            .orElseThrow(() -> new RuntimeException("Rule not found with ID: " + ruleId));

        return communityMapper.toCommunityRuleResponse(rule);
    }

    public List<CommunityRuleResponse> getTopVotedRules(int limit) {
        log.info("Getting top voted rules with limit: {}", limit);

        return communityRuleRepository.findTopVotedRules(limit)
            .stream()
            .map(communityMapper::toCommunityRuleResponse)
            .toList();
    }

    public List<CommunityRuleResponse> getRecentRules(int limit) {
        log.info("Getting recent rules with limit: {}", limit);

        return communityRuleRepository.findAll()
            .stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(limit)
            .map(communityMapper::toCommunityRuleResponse)
            .toList();
    }

    // Private helper methods
    
    private Double calculateCommunityHealth(Long activeUsers, Long totalUsers, Long flaggedMessages, Long totalMessages) {
        if (totalUsers == 0) return 0.0;
        
        // Activity component (0-40 points)
        Double activityScore = (activeUsers.doubleValue() / totalUsers) * 40;
        
        // Content quality component (0-40 points)
        Double contentScore = totalMessages > 0 ? 
            (1 - (flaggedMessages.doubleValue() / totalMessages)) * 40 : 40;
        
        // Engagement component (0-20 points) - based on rules and voting activity
        Long totalRules = communityRuleRepository.count();
        Double engagementScore = Math.min(totalRules.doubleValue() / 10, 1.0) * 20;
        
        return Math.min(100.0, activityScore + contentScore + engagementScore);
    }
    
    private Integer calculateActivationThreshold() {
        // Dynamic threshold based on community size
        Long activeUsers = userRepository.countActiveUsers();
        return Math.max(3, (int) (activeUsers * 0.1)); // 10% of active users
    }
    
    private void saveMetric(String metricName, BigDecimal value) {
        SystemMetric metric = new SystemMetric();
        metric.setMetricName(metricName);
        metric.setMetricValue(value);
        metric.setMetricType(SystemMetric.MetricType.ACTIVITY);
        metric.setCalculationPeriod(SystemMetric.CalculationPeriod.REAL_TIME);
        metric.setMetadata("{}");
        systemMetricRepository.save(metric);
    }
}

