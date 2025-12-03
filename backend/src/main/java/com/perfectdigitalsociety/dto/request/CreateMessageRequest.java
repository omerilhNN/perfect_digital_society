package com.perfectdigitalsociety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMessageRequest {
    
    @NotBlank(message = "Message content is required")
    @Size(max = 10000, message = "Message content must not exceed 10000 characters")
    private String content;
    
    @NotBlank(message = "Message type is required")
    private String messageType; // PUBLIC, PRIVATE, SYSTEM
}