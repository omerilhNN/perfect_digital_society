package com.perfectdigitalsociety.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kullanıcı bilgileri response modeli")
public class UserResponse {
    
    @Schema(description = "Kullanıcı ID'si", example = "1")
    private Long id;

    @Schema(description = "Kullanıcı adı", example = "john_doe")
    private String username;

    @Schema(description = "E-posta adresi", example = "john.doe@example.com")
    private String email;

    @Schema(description = "İsim", example = "John")
    private String firstName;

    @Schema(description = "Soyisim", example = "Doe")
    private String lastName;

    @Schema(description = "Kullanıcı rolü", example = "USER", allowableValues = {"USER", "MODERATOR", "ADMIN"})
    private String role;

    @Schema(description = "Özgürlük skoru", example = "75")
    private Integer freedomScore;

    @Schema(description = "Güvenlik skoru", example = "80")
    private Integer securityScore;

    @Schema(description = "İtibar skoru", example = "90")
    private Integer reputationScore;

    @Schema(description = "Hesap aktif durumu", example = "true")
    private Boolean isActive;

    @Schema(description = "Hesap oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Son giriş tarihi", example = "2024-12-02T14:45:00")
    private LocalDateTime lastLoginAt;
}