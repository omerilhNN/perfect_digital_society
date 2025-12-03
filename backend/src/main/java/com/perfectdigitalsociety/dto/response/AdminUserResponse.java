package com.perfectdigitalsociety.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Integer freedomScore;
    private Integer securityScore;
    private Integer reputationScore;
    private Boolean isActive;
    private Integer messageCount;
    private Integer flagCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}