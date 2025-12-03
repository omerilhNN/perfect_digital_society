package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRuleResponse {
    
    private Long id;
    private String title;
    private String description;
    private String ruleType;
    private Integer priority;
    private Integer threshold;
    private String action;
    private Boolean isActive;
    private Integer votes;
    private Long createdBy;
    private String createdByUsername;
    private LocalDateTime createdAt;
}