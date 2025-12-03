package com.perfectdigitalsociety.repository;

import com.perfectdigitalsociety.entity.CommunityRule;
import com.perfectdigitalsociety.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommunityRuleRepository extends JpaRepository<CommunityRule, Long> {
    
    // Custom query methods as specified in documentation
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.isActive = true ORDER BY cr.priority DESC")
    List<CommunityRule> findActiveRules();
    
    List<CommunityRule> findByPriority(Integer priority);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.ruleType = :type ORDER BY cr.priority DESC")
    List<CommunityRule> findByType(@Param("type") CommunityRule.RuleType type);

    // Additional custom queries
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.isActive = true ORDER BY cr.priority DESC")
    List<CommunityRule> findAllActiveRulesOrderedByPriority();
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.ruleType = :type AND cr.isActive = true ORDER BY cr.priority DESC")
    List<CommunityRule> findActiveRulesByType(@Param("type") CommunityRule.RuleType type);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.priority >= :minPriority ORDER BY cr.priority DESC")
    List<CommunityRule> findRulesByMinimumPriority(@Param("minPriority") Integer minPriority);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.createdBy = :user ORDER BY cr.createdAt DESC")
    List<CommunityRule> findByCreatedBy(@Param("user") User user);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.createdBy.id = :userId ORDER BY cr.createdAt DESC")
    List<CommunityRule> findByCreatedByUserId(@Param("userId") Long userId);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.action = :action AND cr.isActive = true")
    List<CommunityRule> findActiveRulesByAction(@Param("action") CommunityRule.Action action);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.threshold <= :value AND cr.isActive = true ORDER BY cr.priority DESC")
    List<CommunityRule> findRulesByThresholdLimit(@Param("value") Integer value);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.votes >= :minVotes ORDER BY cr.votes DESC")
    List<CommunityRule> findRulesByMinimumVotes(@Param("minVotes") Integer minVotes);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.createdAt BETWEEN :startDate AND :endDate ORDER BY cr.createdAt DESC")
    List<CommunityRule> findRulesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.title LIKE %:keyword% OR cr.description LIKE %:keyword%")
    List<CommunityRule> findRulesByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(cr) FROM CommunityRule cr WHERE cr.isActive = true")
    Long countActiveRules();
    
    @Query("SELECT COUNT(cr) FROM CommunityRule cr WHERE cr.ruleType = :type AND cr.isActive = true")
    Long countActiveRulesByType(@Param("type") CommunityRule.RuleType type);
    
    @Query("SELECT AVG(cr.votes) FROM CommunityRule cr WHERE cr.isActive = true")
    Double getAverageVotes();
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.isActive = true ORDER BY cr.votes DESC LIMIT :limit")
    List<CommunityRule> findTopVotedRules(@Param("limit") int limit);
    
    @Query("SELECT cr FROM CommunityRule cr WHERE cr.isActive = true ORDER BY cr.createdAt DESC LIMIT :limit")
    List<CommunityRule> findMostRecentRules(@Param("limit") int limit);
}