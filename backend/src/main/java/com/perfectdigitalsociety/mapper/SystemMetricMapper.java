package com.perfectdigitalsociety.mapper;

import com.perfectdigitalsociety.dto.response.SystemMetricsResponse;
import com.perfectdigitalsociety.entity.SystemMetric;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SystemMetricMapper {
    
    /**
     * Create SystemMetricsResponse with provided values
     */
    @Mapping(target = "totalUsers", source = "totalUsers")
    @Mapping(target = "activeUsers", source = "activeUsers")
    @Mapping(target = "totalMessages", source = "totalMessages")
    @Mapping(target = "totalBalanceEvents", source = "totalBalanceEvents")
    @Mapping(target = "systemFreedomLevel", source = "systemFreedomLevel")
    @Mapping(target = "systemSecurityLevel", source = "systemSecurityLevel")
    @Mapping(target = "averageUserBalance", source = "averageUserBalance")
    @Mapping(target = "systemHealth", source = "systemHealth")
    @Mapping(target = "uptime", source = "uptime")
    @Mapping(target = "lastSystemEvent", source = "lastSystemEvent")
    SystemMetricsResponse toSystemMetricsResponse(
        Integer totalUsers,
        Integer activeUsers,
        Integer totalMessages,
        Integer totalBalanceEvents,
        Integer systemFreedomLevel,
        Integer systemSecurityLevel,
        Double averageUserBalance,
        Double systemHealth,
        Long uptime,
        LocalDateTime lastSystemEvent
    );
    
    /**
     * Convert SystemMetric.MetricType enum to string
     */
    default String mapMetricType(SystemMetric.MetricType metricType) {
        return metricType != null ? metricType.toString() : null;
    }
    
    /**
     * Convert string to SystemMetric.MetricType enum
     */
    default SystemMetric.MetricType mapMetricType(String metricType) {
        return metricType != null ? SystemMetric.MetricType.valueOf(metricType.toUpperCase()) : null;
    }
    
    /**
     * Convert SystemMetric.CalculationPeriod enum to string
     */
    default String mapCalculationPeriod(SystemMetric.CalculationPeriod period) {
        return period != null ? period.toString() : null;
    }
    
    /**
     * Convert string to SystemMetric.CalculationPeriod enum
     */
    default SystemMetric.CalculationPeriod mapCalculationPeriod(String period) {
        return period != null ? SystemMetric.CalculationPeriod.valueOf(period.toUpperCase()) : null;
    }
}