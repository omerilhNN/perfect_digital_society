package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMetricsResponse {
    
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer totalMessages;
    private Integer flaggedMessages;
    private Double averageFreedomScore;
    private Double averageSecurityScore;
    private Double communityHealth;
    private LocalDateTime lastCalculated;
}