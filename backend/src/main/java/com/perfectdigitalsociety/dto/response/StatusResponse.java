package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse {
    
    private Boolean success;
    private String message;
    private LocalDateTime timestamp;
}