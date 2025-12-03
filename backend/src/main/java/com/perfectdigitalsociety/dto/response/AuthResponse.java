package com.perfectdigitalsociety.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kimlik doğrulama sonucu ve JWT token bilgileri")
public class AuthResponse {
    
    @Schema(
        description = "JWT access token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huX2RvZSIsImlhdCI6MTYyMzc1NjgwMCwiZXhwIjoxNjIzODQzMjAwfQ.signature"
    )
    private String token;

    @Schema(description = "Giriş yapan kullanıcının bilgileri")
    private UserResponse user;

    @Schema(description = "Token'ın son kullanma tarihi", example = "2024-12-03T14:45:00")
    private LocalDateTime expiresAt;
}