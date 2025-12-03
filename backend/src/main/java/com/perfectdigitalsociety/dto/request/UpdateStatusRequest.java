package com.perfectdigitalsociety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {
    
    @NotBlank(message = "Status is required")
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    
    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;
}