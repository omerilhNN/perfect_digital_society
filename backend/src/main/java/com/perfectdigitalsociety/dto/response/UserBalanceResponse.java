package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponse {
    
    private Long userId;
    private String username;
    private Integer freedomScore;
    private Integer securityScore;
    private Integer reputationScore;
    private Double balanceRatio;
    private LocalDateTime lastUpdated;
}