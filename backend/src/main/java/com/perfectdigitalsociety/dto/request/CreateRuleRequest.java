package com.perfectdigitalsociety.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRuleRequest {
    
    @NotBlank(message = "Rule title is required")
    @Size(max = 100, message = "Rule title must not exceed 100 characters")
    private String title;
    
    @NotBlank(message = "Rule description is required")
    @Size(max = 1000, message = "Rule description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Rule type is required")
    private String ruleType; // FREEDOM, SECURITY, BALANCE
    
    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be at least 1")
    private Integer priority;
    
    @NotNull(message = "Threshold is required")
    @Min(value = 1, message = "Threshold must be at least 1")
    private Integer threshold;
    
    @NotBlank(message = "Action is required")
    private String action; // WARN, RESTRICT, SUSPEND
}