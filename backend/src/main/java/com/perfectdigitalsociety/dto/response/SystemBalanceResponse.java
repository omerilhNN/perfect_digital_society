package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemBalanceResponse {
    
    private Integer currentFreedomLevel;
    private Integer currentSecurityLevel;
    private Double balanceScore;
    private LocalDateTime lastUpdated;
    private String trend;
}