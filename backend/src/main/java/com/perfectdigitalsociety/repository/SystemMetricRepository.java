package com.perfectdigitalsociety.repository;

import com.perfectdigitalsociety.entity.SystemMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SystemMetricRepository extends JpaRepository<SystemMetric, Long> {
    
    // Custom query methods as specified in documentation
    List<SystemMetric> findByMetricName(String metricName);
    
    List<SystemMetric> findByCalculationPeriod(SystemMetric.CalculationPeriod calculationPeriod);

    @Query("SELECT sm FROM SystemMetric sm ORDER BY sm.recordedAt DESC LIMIT 20")
    List<SystemMetric> findLatestMetrics();
    
    // Additional custom queries
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricName = :name ORDER BY sm.recordedAt DESC")
    List<SystemMetric> findByMetricNameOrderedByDate(@Param("name") String metricName);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.calculationPeriod = :period ORDER BY sm.recordedAt DESC")
    List<SystemMetric> findByCalculationPeriodOrderedByDate(@Param("period") SystemMetric.CalculationPeriod period);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricType = :type ORDER BY sm.recordedAt DESC")
    List<SystemMetric> findByMetricType(@Param("type") SystemMetric.MetricType type);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricName = :name ORDER BY sm.recordedAt DESC LIMIT 1")
    Optional<SystemMetric> findLatestByMetricName(@Param("name") String metricName);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricType = :type ORDER BY sm.recordedAt DESC LIMIT 1")
    Optional<SystemMetric> findLatestByMetricType(@Param("type") SystemMetric.MetricType type);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.recordedAt BETWEEN :startDate AND :endDate ORDER BY sm.recordedAt DESC")
    List<SystemMetric> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricName = :name AND sm.recordedAt BETWEEN :startDate AND :endDate ORDER BY sm.recordedAt DESC")
    List<SystemMetric> findByMetricNameAndDateRange(@Param("name") String metricName, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricValue >= :minValue ORDER BY sm.metricValue DESC")
    List<SystemMetric> findByMinimumValue(@Param("minValue") BigDecimal minValue);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricValue BETWEEN :minValue AND :maxValue ORDER BY sm.recordedAt DESC")
    List<SystemMetric> findByValueRange(@Param("minValue") BigDecimal minValue, @Param("maxValue") BigDecimal maxValue);
    
    @Query("SELECT DISTINCT sm.metricName FROM SystemMetric sm ORDER BY sm.metricName")
    List<String> findDistinctMetricNames();
    
    @Query("SELECT COUNT(sm) FROM SystemMetric sm WHERE sm.metricType = :type")
    Long countByMetricType(@Param("type") SystemMetric.MetricType type);
    
    @Query("SELECT AVG(sm.metricValue) FROM SystemMetric sm WHERE sm.metricName = :name AND sm.recordedAt >= :since")
    BigDecimal getAverageValueSince(@Param("name") String metricName, @Param("since") LocalDateTime since);
    
    @Query("SELECT MAX(sm.metricValue) FROM SystemMetric sm WHERE sm.metricName = :name")
    BigDecimal getMaxValueByMetricName(@Param("name") String metricName);
    
    @Query("SELECT MIN(sm.metricValue) FROM SystemMetric sm WHERE sm.metricName = :name")
    BigDecimal getMinValueByMetricName(@Param("name") String metricName);
    
    @Query("SELECT sm FROM SystemMetric sm ORDER BY sm.recordedAt DESC LIMIT :limit")
    List<SystemMetric> findMostRecentMetrics(@Param("limit") int limit);
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.recordedAt >= :since ORDER BY sm.recordedAt DESC")
    List<SystemMetric> findMetricsSince(@Param("since") LocalDateTime since);
    
    // Special queries for system health monitoring
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.metricName IN ('system_health', 'balance_score', 'community_health') ORDER BY sm.recordedAt DESC LIMIT 10")
    List<SystemMetric> findLatestHealthMetrics();
    
    @Query("SELECT sm FROM SystemMetric sm WHERE sm.calculationPeriod = 'REAL_TIME' ORDER BY sm.recordedAt DESC LIMIT :limit")
    List<SystemMetric> findLatestRealTimeMetrics(@Param("limit") int limit);
}