package com.perfectdigitalsociety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TriggerBalanceRequest {
    
    @NotBlank(message = "Event type is required")
    private String eventType; // USER_ACTION, SYSTEM_AUTO, ADMIN_MANUAL
    
    @NotBlank(message = "Event description is required")
    @Size(max = 255, message = "Event description must not exceed 255 characters")
    private String description;
}