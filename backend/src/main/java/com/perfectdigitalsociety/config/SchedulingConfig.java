package com.perfectdigitalsociety.config;

import com.perfectdigitalsociety.service.BalanceService;
import com.perfectdigitalsociety.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {
    
    private final BalanceService balanceService;
    private final CommunityService communityService;
    
    /**
     * Automatic balance rebalancing every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void performAutomaticRebalancing() {
        log.info("Starting scheduled automatic rebalancing");
        try {
            balanceService.performAutomaticRebalancing();
            log.info("Scheduled automatic rebalancing completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled automatic rebalancing", e);
        }
    }
    
    /**
     * Community metrics calculation every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes
    public void calculateCommunityMetrics() {
        log.info("Starting scheduled community metrics calculation");
        try {
            communityService.analyzeMetrics();
            log.info("Scheduled community metrics calculation completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled community metrics calculation", e);
        }
    }
    
    /**
     * Rule effectiveness evaluation daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void evaluateRuleEffectiveness() {
        log.info("Starting scheduled rule effectiveness evaluation");
        try {
            communityService.evaluateRuleEffectiveness();
            log.info("Scheduled rule effectiveness evaluation completed successfully");
        } catch (Exception e) {
            log.error("Error during scheduled rule effectiveness evaluation", e);
        }
    }
    
    /**
     * System health check every 15 minutes
     */
    @Scheduled(fixedRate = 900000) // 15 minutes
    public void systemHealthCheck() {
        log.debug("Performing system health check");
        try {
            balanceService.calculateSystemBalance();
            log.debug("System health check completed successfully");
        } catch (Exception e) {
            log.error("Error during system health check", e);
        }
    }
}