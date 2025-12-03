package com.perfectdigitalsociety.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SystemMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "metric_name", nullable = false, length = 50)
    private String metricName;
    
    @Column(name = "metric_value", precision = 10, scale = 2)
    private BigDecimal metricValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false)
    private MetricType metricType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_period", nullable = false)
    private CalculationPeriod calculationPeriod;
    
    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON metadata
    
    @CreatedDate
    @Column(name = "recorded_at", updatable = false)
    private LocalDateTime recordedAt;
    
    public enum MetricType {
        BALANCE, ACTIVITY, HEALTH
    }
    
    public enum CalculationPeriod {
        REAL_TIME, HOURLY, DAILY
    }
}