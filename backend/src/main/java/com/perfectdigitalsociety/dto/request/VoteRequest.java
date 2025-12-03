package com.perfectdigitalsociety.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    
    @NotNull(message = "Rule ID is required")
    private Long ruleId;
    
    @NotNull(message = "Vote is required")
    private Boolean vote; // true for positive, false for negative
}