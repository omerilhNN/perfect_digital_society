package com.perfectdigitalsociety.repository;

import com.perfectdigitalsociety.entity.BalanceEvent;
import com.perfectdigitalsociety.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BalanceEventRepository extends JpaRepository<BalanceEvent, Long> {
    
    // Custom query methods as specified in documentation
    @Query("SELECT be FROM BalanceEvent be ORDER BY be.createdAt DESC LIMIT 10")
    List<BalanceEvent> findRecentEvents();
    
    List<BalanceEvent> findByTriggerType(BalanceEvent.TriggerType triggerType);
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.triggerType IN ('SYSTEM_AUTO') ORDER BY be.createdAt DESC")
    List<BalanceEvent> findSystemEvents();
    
    // Additional custom queries
    @Query("SELECT be FROM BalanceEvent be WHERE be.createdAt >= :since ORDER BY be.createdAt DESC")
    List<BalanceEvent> findRecentEventsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.triggerType = 'SYSTEM_AUTO' ORDER BY be.createdAt DESC")
    List<BalanceEvent> findAllSystemEvents();
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.triggerType = 'ADMIN_MANUAL' ORDER BY be.createdAt DESC")
    List<BalanceEvent> findAdminEvents();
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.triggeredBy = :user ORDER BY be.createdAt DESC")
    List<BalanceEvent> findByTriggeredBy(@Param("user") User user);
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.triggeredBy.id = :userId ORDER BY be.createdAt DESC")
    List<BalanceEvent> findByTriggeredByUserId(@Param("userId") Long userId);
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.createdAt BETWEEN :startDate AND :endDate ORDER BY be.createdAt DESC")
    List<BalanceEvent> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.newFreedomLevel != be.previousFreedomLevel ORDER BY be.createdAt DESC")
    List<BalanceEvent> findFreedomLevelChanges();
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.newSecurityLevel != be.previousSecurityLevel ORDER BY be.createdAt DESC")
    List<BalanceEvent> findSecurityLevelChanges();
    
    @Query("SELECT be FROM BalanceEvent be ORDER BY be.createdAt DESC LIMIT :limit")
    List<BalanceEvent> findMostRecentEvents(@Param("limit") int limit);
    
    @Query("SELECT COUNT(be) FROM BalanceEvent be WHERE be.triggerType = :type")
    Long countByTriggerType(@Param("type") BalanceEvent.TriggerType type);
    
    @Query("SELECT COUNT(be) FROM BalanceEvent be WHERE be.createdAt >= :since")
    Long countEventsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(be.newFreedomLevel) FROM BalanceEvent be WHERE be.createdAt >= :since")
    Double getAverageFreedomLevelSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(be.newSecurityLevel) FROM BalanceEvent be WHERE be.createdAt >= :since")
    Double getAverageSecurityLevelSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT be FROM BalanceEvent be WHERE be.affectedUsers IS NOT NULL AND be.affectedUsers != '' ORDER BY be.createdAt DESC")
    List<BalanceEvent> findEventsWithAffectedUsers();
}