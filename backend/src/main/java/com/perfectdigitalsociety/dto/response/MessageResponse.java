package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private String messageType;
    private Integer freedomImpact;
    private Integer securityImpact;
    private Integer flagCount;
    private Boolean isVisible;
    private String moderationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}