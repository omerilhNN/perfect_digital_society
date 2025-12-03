package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetricsResponse {
    
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer totalMessages;
    private Integer totalBalanceEvents;
    private Integer systemFreedomLevel;
    private Integer systemSecurityLevel;
    private Double averageUserBalance;
    private Double systemHealth;
    private Long uptime;
    private LocalDateTime lastSystemEvent;
}