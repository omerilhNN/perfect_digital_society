package com.perfectdigitalsociety.service;

import com.perfectdigitalsociety.dto.request.AdjustBalanceRequest;
import com.perfectdigitalsociety.dto.request.TriggerBalanceRequest;
import com.perfectdigitalsociety.dto.response.BalanceEventResponse;
import com.perfectdigitalsociety.dto.response.SystemBalanceResponse;
import com.perfectdigitalsociety.dto.response.UserBalanceResponse;
import com.perfectdigitalsociety.entity.BalanceEvent;
import com.perfectdigitalsociety.entity.Message;
import com.perfectdigitalsociety.entity.SystemMetric;
import com.perfectdigitalsociety.entity.User;
import com.perfectdigitalsociety.exception.UserNotFoundException;
import com.perfectdigitalsociety.mapper.BalanceMapper;
import com.perfectdigitalsociety.repository.BalanceEventRepository;
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
public class BalanceService {
    
    private final BalanceEventRepository balanceEventRepository;
    private final SystemMetricRepository systemMetricRepository;
    private final UserRepository userRepository;
    private final BalanceMapper balanceMapper;
    
    // Business Logic Methods as specified in documentation
    
    /**
     * Calculate system-wide balance
     */
    public SystemBalanceResponse calculateSystemBalance() {
        log.info("Calculating system-wide balance");
        
        // Get current system metrics
        List<User> activeUsers = userRepository.findAllActiveUsers();
        
        if (activeUsers.isEmpty()) {
            return createDefaultSystemBalance();
        }
        
        // Calculate average freedom and security levels
        Double avgFreedom = activeUsers.stream()
            .mapToDouble(User::getFreedomScore)
            .average()
            .orElse(50.0);
        
        Double avgSecurity = activeUsers.stream()
            .mapToDouble(User::getSecurityScore)
            .average()
            .orElse(50.0);
        
        // Calculate balance score (closer to 1.0 means better balance)
        Double balanceScore = calculateBalanceScore(avgFreedom, avgSecurity);
        
        // Determine trend
        String trend = determineTrend(avgFreedom, avgSecurity);
        
        // Save system metrics
        saveSystemMetric("system_freedom_level", BigDecimal.valueOf(avgFreedom));
        saveSystemMetric("system_security_level", BigDecimal.valueOf(avgSecurity));
        saveSystemMetric("system_balance_score", BigDecimal.valueOf(balanceScore));
        
        SystemBalanceResponse response = new SystemBalanceResponse();
        response.setCurrentFreedomLevel(avgFreedom.intValue());
        response.setCurrentSecurityLevel(avgSecurity.intValue());
        response.setBalanceScore(balanceScore);
        response.setLastUpdated(LocalDateTime.now());
        response.setTrend(trend);
        
        log.info("System balance calculated - Freedom: {}, Security: {}, Balance Score: {}", 
                avgFreedom.intValue(), avgSecurity.intValue(), balanceScore);

        return response;
    }
    
    /**
     * Trigger balance event
     */
    public BalanceEventResponse triggerBalanceEvent(Long userId, TriggerBalanceRequest request) {
        log.info("Triggering balance event by user ID: {}", userId);
        
        User triggerUser = null;
        if (userId != null) {
            triggerUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        }
        
        // Get current system balance
        SystemBalanceResponse currentBalance = calculateSystemBalance();
        
        // Create balance event
        BalanceEvent event = new BalanceEvent();
        event.setTriggerType(BalanceEvent.TriggerType.valueOf(request.getEventType().toUpperCase()));
        event.setEventDescription(request.getDescription());
        event.setPreviousFreedomLevel(currentBalance.getCurrentFreedomLevel());
        event.setPreviousSecurityLevel(currentBalance.getCurrentSecurityLevel());
        event.setTriggeredBy(triggerUser);
        event.setAffectedUsers("[]"); // JSON array of affected users
        
        // Apply balance adjustment algorithm
        BalanceAdjustment adjustment = calculateBalanceAdjustment(event.getTriggerType(), request.getDescription());

        // Apply adjustments to system
        applySystemAdjustment(adjustment);
        
        // Get new balance after adjustment
        SystemBalanceResponse newBalance = calculateSystemBalance();
        event.setNewFreedomLevel(newBalance.getCurrentFreedomLevel());
        event.setNewSecurityLevel(newBalance.getCurrentSecurityLevel());
        
        BalanceEvent savedEvent = balanceEventRepository.save(event);

        log.info("Balance event triggered successfully with ID: {}", savedEvent.getId());
        return balanceMapper.toBalanceEventResponse(savedEvent);
    }
    
    /**
     * Analyze impact of a message or user action
     */
    public void analyzeImpact(Message message) {
        log.info("Analyzing impact of message ID: {}", message.getId());
        
        // Get current user balance
        User user = message.getUser();
        Integer previousFreedom = user.getFreedomScore();
        Integer previousSecurity = user.getSecurityScore();
        
        // Apply message impact to user scores
        Integer newFreedom = Math.max(0, Math.min(100, previousFreedom + message.getFreedomImpact()));
        Integer newSecurity = Math.max(0, Math.min(100, previousSecurity + message.getSecurityImpact()));
        
        user.setFreedomScore(newFreedom);
        user.setSecurityScore(newSecurity);
        userRepository.save(user);
        
        // Create balance event
        BalanceEvent event = new BalanceEvent();
        event.setTriggerType(BalanceEvent.TriggerType.USER_ACTION);
        event.setEventDescription("Message impact analysis for message ID: " + message.getId());
        event.setPreviousFreedomLevel(previousFreedom);
        event.setNewFreedomLevel(newFreedom);
        event.setPreviousSecurityLevel(previousSecurity);
        event.setNewSecurityLevel(newSecurity);
        event.setTriggeredBy(user);
        event.setAffectedUsers("[" + user.getId() + "]");
        
        balanceEventRepository.save(event);
        
        // Check if system-wide rebalancing is needed
        if (Math.abs(message.getFreedomImpact()) > 20 || Math.abs(message.getSecurityImpact()) > 20) {
            triggerSystemRebalancing("High impact message detected");
        }
        
        log.info("Impact analysis completed for message ID: {}", message.getId());
    }
    
    /**
     * Manually adjust balance (admin function)
     */
    public BalanceEventResponse adjustBalance(Long userId, Integer freedomAdjustment, Integer securityAdjustment, String reason) {
        log.info("Adjusting balance for user ID: {} - Freedom: {}, Security: {}", userId, freedomAdjustment, securityAdjustment);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        Integer previousFreedom = user.getFreedomScore();
        Integer previousSecurity = user.getSecurityScore();
        
        // Apply adjustments with bounds checking
        Integer newFreedom = Math.max(0, Math.min(100, previousFreedom + freedomAdjustment));
        Integer newSecurity = Math.max(0, Math.min(100, previousSecurity + securityAdjustment));
        
        user.setFreedomScore(newFreedom);
        user.setSecurityScore(newSecurity);
        userRepository.save(user);
        
        // Create balance event
        BalanceEvent event = new BalanceEvent();
        event.setTriggerType(BalanceEvent.TriggerType.ADMIN_MANUAL);
        event.setEventDescription(reason);
        event.setPreviousFreedomLevel(previousFreedom);
        event.setNewFreedomLevel(newFreedom);
        event.setPreviousSecurityLevel(previousSecurity);
        event.setNewSecurityLevel(newSecurity);
        event.setTriggeredBy(user);
        event.setAffectedUsers("[" + userId + "]");
        
        BalanceEvent savedEvent = balanceEventRepository.save(event);

        log.info("Balance adjusted successfully for user ID: {}", userId);
        return balanceMapper.toBalanceEventResponse(savedEvent);
    }
    
    /**
     * Calculate individual user balance
     */
    public UserBalanceResponse calculateUserBalance(Long userId) {
        log.info("Calculating balance for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Get user activity metrics - handle null collections
        Integer messageCount = (user.getMessages() != null) ? user.getMessages().size() : 0;

        // Calculate new scores based on activity and community participation
        Integer newFreedomScore = calculateUserFreedomScore(user, messageCount);
        Integer newSecurityScore = calculateUserSecurityScore(user, messageCount);
        Integer newReputationScore = calculateUserReputationScore(user);

        // Update user scores
        user.setFreedomScore(newFreedomScore);
        user.setSecurityScore(newSecurityScore);
        user.setReputationScore(newReputationScore);

        userRepository.save(user);

        // Calculate balance ratio
        Double balanceRatio = newSecurityScore != 0 ? (double) newFreedomScore / newSecurityScore : 1.0;

        UserBalanceResponse response = new UserBalanceResponse();
        response.setUserId(userId);
        response.setUsername(user.getUsername());
        response.setFreedomScore(newFreedomScore);
        response.setSecurityScore(newSecurityScore);
        response.setReputationScore(newReputationScore);
        response.setBalanceRatio(balanceRatio);
        response.setLastUpdated(LocalDateTime.now());

        log.info("User balance calculated - Freedom: {}, Security: {}, Reputation: {}",
                newFreedomScore, newSecurityScore, newReputationScore);

        return response;
    }

    public List<BalanceEventResponse> getBalanceHistory(int limit) {
        log.info("Getting balance history with limit: {}", limit);
        return balanceEventRepository.findMostRecentEvents(limit)
            .stream()
            .map(balanceMapper::toBalanceEventResponse)
            .toList();
    }
    
    public UserBalanceResponse getUserBalance(Long userId) {
        log.info("Getting balance for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        Double balanceRatio = user.getSecurityScore() != 0 ? 
            (double) user.getFreedomScore() / user.getSecurityScore() : 1.0;
        
        UserBalanceResponse response = new UserBalanceResponse();
        response.setUserId(userId);
        response.setUsername(user.getUsername());
        response.setFreedomScore(user.getFreedomScore());
        response.setSecurityScore(user.getSecurityScore());
        response.setReputationScore(user.getReputationScore());
        response.setBalanceRatio(balanceRatio);
        response.setLastUpdated(LocalDateTime.now());
        
        return response;
    }
    
    public void performAutomaticRebalancing() {
        log.info("Performing automatic system rebalancing");
        
        SystemBalanceResponse currentBalance = calculateSystemBalance();
        
        // Check if rebalancing is needed
        if (isRebalancingNeeded(currentBalance)) {
            BalanceAdjustment adjustment = calculateGlobalAdjustment(currentBalance);
            applyGlobalAdjustment(adjustment);
            
            // Create system event
            BalanceEvent event = new BalanceEvent();
            event.setTriggerType(BalanceEvent.TriggerType.SYSTEM_AUTO);
            event.setEventDescription("Automatic system rebalancing");
            event.setPreviousFreedomLevel(currentBalance.getCurrentFreedomLevel());
            event.setPreviousSecurityLevel(currentBalance.getCurrentSecurityLevel());
            
            SystemBalanceResponse newBalance = calculateSystemBalance();
            event.setNewFreedomLevel(newBalance.getCurrentFreedomLevel());
            event.setNewSecurityLevel(newBalance.getCurrentSecurityLevel());
            event.setAffectedUsers("all");
            
            balanceEventRepository.save(event);
            
            log.info("Automatic rebalancing completed");
        }
    }
    
    // Private helper methods
    
    private SystemBalanceResponse createDefaultSystemBalance() {
        SystemBalanceResponse response = new SystemBalanceResponse();
        response.setCurrentFreedomLevel(50);
        response.setCurrentSecurityLevel(50);
        response.setBalanceScore(1.0);
        response.setLastUpdated(LocalDateTime.now());
        response.setTrend("STABLE");
        return response;
    }
    
    private Double calculateBalanceScore(Double freedom, Double security) {
        // Perfect balance score calculation
        // Closer to 1.0 means better balance between freedom and security
        if (freedom == 0 && security == 0) return 1.0;
        if (security == 0) return 0.1; // Heavy freedom bias
        if (freedom == 0) return 0.1; // Heavy security bias
        
        Double ratio = freedom / security;
        // Score is higher when ratio is closer to 1.0
        return 1.0 / (1.0 + Math.abs(ratio - 1.0));
    }
    
    private String determineTrend(Double currentFreedom, Double currentSecurity) {
        // Get previous balance metrics
        SystemMetric prevFreedomMetric = systemMetricRepository
            .findLatestByMetricName("system_freedom_level")
            .orElse(null);
        
        if (prevFreedomMetric == null) {
            return "STABLE";
        }
        
        Double previousFreedom = prevFreedomMetric.getMetricValue().doubleValue();
        Double freedomChange = currentFreedom - previousFreedom;
        
        if (Math.abs(freedomChange) < 2) {
            return "STABLE";
        } else if (freedomChange > 0) {
            return "FREEDOM_INCREASING";
        } else {
            return "SECURITY_INCREASING";
        }
    }
    
    private void saveSystemMetric(String metricName, BigDecimal value) {
        SystemMetric metric = new SystemMetric();
        metric.setMetricName(metricName);
        metric.setMetricValue(value);
        metric.setMetricType(SystemMetric.MetricType.BALANCE);
        metric.setCalculationPeriod(SystemMetric.CalculationPeriod.REAL_TIME);
        metric.setMetadata("{}");
        systemMetricRepository.save(metric);
    }
    
    private BalanceAdjustment calculateBalanceAdjustment(BalanceEvent.TriggerType triggerType, String description) {
        // Dynamic balance adjustment based on trigger type and context
        BalanceAdjustment adjustment = new BalanceAdjustment();
        
        switch (triggerType) {
            case USER_ACTION -> {
                adjustment.setFreedomAdjustment(2);
                adjustment.setSecurityAdjustment(-1);
            }
            case SYSTEM_AUTO -> {
                adjustment.setFreedomAdjustment(0);
                adjustment.setSecurityAdjustment(1);
            }
            case ADMIN_MANUAL -> {
                // Parse adjustment from description or use default
                adjustment.setFreedomAdjustment(0);
                adjustment.setSecurityAdjustment(0);
            }
        }
        
        return adjustment;
    }
    
    private void applySystemAdjustment(BalanceAdjustment adjustment) {
        // Apply small adjustments to all active users
        List<User> activeUsers = userRepository.findAllActiveUsers();
        
        for (User user : activeUsers) {
            Integer newFreedom = Math.max(0, Math.min(100, 
                user.getFreedomScore() + adjustment.getFreedomAdjustment()));
            Integer newSecurity = Math.max(0, Math.min(100, 
                user.getSecurityScore() + adjustment.getSecurityAdjustment()));
            
            user.setFreedomScore(newFreedom);
            user.setSecurityScore(newSecurity);
        }
        
        userRepository.saveAll(activeUsers);
    }
    
    private void triggerSystemRebalancing(String reason) {
        log.info("Triggering system rebalancing: {}", reason);
        performAutomaticRebalancing();
    }
    
    private boolean isRebalancingNeeded(SystemBalanceResponse balance) {
        // Rebalancing needed if balance score is too low or extreme imbalance
        return balance.getBalanceScore() < 0.7 ||
               Math.abs(balance.getCurrentFreedomLevel() - balance.getCurrentSecurityLevel()) > 30;
    }
    
    private BalanceAdjustment calculateGlobalAdjustment(SystemBalanceResponse currentBalance) {
        BalanceAdjustment adjustment = new BalanceAdjustment();
        
        Integer freedomLevel = currentBalance.getCurrentFreedomLevel();
        Integer securityLevel = currentBalance.getCurrentSecurityLevel();
        
        // Adjust towards balance
        if (freedomLevel > securityLevel + 10) {
            adjustment.setFreedomAdjustment(-2);
            adjustment.setSecurityAdjustment(1);
        } else if (securityLevel > freedomLevel + 10) {
            adjustment.setFreedomAdjustment(1);
            adjustment.setSecurityAdjustment(-2);
        }
        
        return adjustment;
    }
    
    private void applyGlobalAdjustment(BalanceAdjustment adjustment) {
        applySystemAdjustment(adjustment);
    }
    
    // Helper class for balance adjustments
    private static class BalanceAdjustment {
        private Integer freedomAdjustment = 0;
        private Integer securityAdjustment = 0;
        
        public Integer getFreedomAdjustment() { return freedomAdjustment; }
        public void setFreedomAdjustment(Integer freedomAdjustment) { this.freedomAdjustment = freedomAdjustment; }
        public Integer getSecurityAdjustment() { return securityAdjustment; }
        public void setSecurityAdjustment(Integer securityAdjustment) { this.securityAdjustment = securityAdjustment; }
    }

    private Integer calculateUserFreedomScore(User user, Integer messageCount) {
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

    private Integer calculateUserSecurityScore(User user, Integer messageCount) {
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

    private Integer calculateUserReputationScore(User user) {
        // Reputation based on community feedback and contribution
        Integer baseReputation = user.getReputationScore() != null ? user.getReputationScore() : 0;

        // This would be enhanced with actual community feedback data
        // For now, simple calculation based on account status
        Integer statusBonus = user.getIsActive() ? 10 : 0;

        return Math.max(0, baseReputation + statusBonus);
    }
}
