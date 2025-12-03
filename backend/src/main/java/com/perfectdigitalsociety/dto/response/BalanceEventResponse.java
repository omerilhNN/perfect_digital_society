package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceEventResponse {
    
    private Long id;
    private String triggerType;
    private String eventDescription;
    private Integer previousFreedomLevel;
    private Integer newFreedomLevel;
    private Integer previousSecurityLevel;
    private Integer newSecurityLevel;
    private Long triggeredBy;
    private String triggeredByUsername;
    private String affectedUsers;
    private LocalDateTime createdAt;
}