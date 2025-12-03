package com.perfectdigitalsociety.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMessageRequest {
    
    @NotBlank(message = "Message content is required")
    @Size(max = 10000, message = "Message content must not exceed 10000 characters")
    private String content;
}