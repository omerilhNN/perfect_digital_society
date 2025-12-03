package com.perfectdigitalsociety.repository;

import com.perfectdigitalsociety.entity.Message;
import com.perfectdigitalsociety.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Custom query methods as specified in documentation
    List<Message> findByUser(User user);
    
    List<Message> findByUserId(Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.flagCount > 0 ORDER BY m.flagCount DESC")
    List<Message> findFlaggedMessages();
    
    @Query("SELECT m FROM Message m WHERE m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt DESC")
    List<Message> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Additional custom queries
    @Query("SELECT m FROM Message m WHERE m.flagCount > 0 ORDER BY m.flagCount DESC")
    List<Message> findAllFlaggedMessages();
    
    @Query("SELECT m FROM Message m WHERE m.flagCount >= :threshold")
    List<Message> findMessagesByFlagThreshold(@Param("threshold") Integer threshold);
    
    @Query("SELECT m FROM Message m WHERE m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt DESC")
    List<Message> findMessagesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT m FROM Message m WHERE m.moderationStatus = :status")
    List<Message> findByModerationStatus(@Param("status") Message.ModerationStatus status);
    
    @Query("SELECT m FROM Message m WHERE m.messageType = :type AND m.isVisible = true")
    List<Message> findByMessageTypeAndVisible(@Param("type") Message.MessageType type);
    
    @Query("SELECT m FROM Message m WHERE m.isVisible = true ORDER BY m.createdAt DESC")
    List<Message> findVisibleMessagesOrderedByDate();
    
    @Query("SELECT m FROM Message m WHERE m.user.id = :userId AND m.isVisible = true ORDER BY m.createdAt DESC")
    List<Message> findVisibleMessagesByUser(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.content LIKE %:keyword% AND m.isVisible = true")
    List<Message> findByContentContaining(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.moderationStatus = 'PENDING'")
    Long countPendingModeration();
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.user.id = :userId")
    Long countMessagesByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.flagCount > 0")
    Long countFlaggedMessages();
    
    @Query("SELECT SUM(m.freedomImpact) FROM Message m WHERE m.user.id = :userId")
    Integer getTotalFreedomImpactByUser(@Param("userId") Long userId);
    
    @Query("SELECT SUM(m.securityImpact) FROM Message m WHERE m.user.id = :userId")
    Integer getTotalSecurityImpactByUser(@Param("userId") Long userId);
    
    @Query("SELECT m FROM Message m WHERE m.createdAt >= :since ORDER BY m.flagCount DESC")
    List<Message> findRecentMessagesOrderedByFlags(@Param("since") LocalDateTime since);
}