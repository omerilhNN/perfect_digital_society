package com.perfectdigitalsociety.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "balance_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BalanceEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false)
    private TriggerType triggerType;
    
    @Column(name = "event_description", length = 255)
    private String eventDescription;
    
    @Column(name = "previous_freedom_level")
    private Integer previousFreedomLevel;
    
    @Column(name = "new_freedom_level")
    private Integer newFreedomLevel;
    
    @Column(name = "previous_security_level")
    private Integer previousSecurityLevel;
    
    @Column(name = "new_security_level")
    private Integer newSecurityLevel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_by")
    private User triggeredBy;
    
    @Column(name = "affected_users", columnDefinition = "TEXT")
    private String affectedUsers; // JSON array as string
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum TriggerType {
        USER_ACTION, SYSTEM_AUTO, ADMIN_MANUAL
    }
}