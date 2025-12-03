package com.perfectdigitalsociety.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "Kullanıcı varlığı - Sistemdeki tüm kullanıcıları temsil eder")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Benzersiz kullanıcı ID'si", example = "1")
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    @Schema(description = "Benzersiz kullanıcı adı", example = "john_doe", maxLength = 50)
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    @Schema(description = "Benzersiz e-posta adresi", example = "john.doe@example.com", maxLength = 100)
    private String email;
    
    @Column(nullable = false, length = 255, name = "password_hash")
    @Schema(description = "Hashlenenmiş kullanıcı şifresi", hidden = true)
    private String passwordHash;
    
    @Column(length = 50, name = "first_name")
    @Schema(description = "Kullanıcının adı", example = "John", maxLength = 50)
    private String firstName;
    
    @Column(length = 50, name = "last_name")
    @Schema(description = "Kullanıcının soyadı", example = "Doe", maxLength = 50)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Kullanıcı rolü", example = "USER", allowableValues = {"USER", "MODERATOR", "ADMIN"})
    private Role role = Role.USER;
    
    @Column(name = "freedom_score")
    @Schema(description = "Kullanıcının özgürlük skoru (0-100)", example = "75", minimum = "0", maximum = "100")
    private Integer freedomScore = 0;
    
    @Column(name = "security_score")
    @Schema(description = "Kullanıcının güvenlik skoru (0-100)", example = "80", minimum = "0", maximum = "100")
    private Integer securityScore = 0;
    
    @Column(name = "reputation_score")
    @Schema(description = "Kullanıcının itibar skoru (0-100)", example = "90", minimum = "0", maximum = "100")
    private Integer reputationScore = 0;
    
    @Column(name = "is_active")
    @Schema(description = "Hesap aktif durumu", example = "true")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Schema(description = "Hesap oluşturulma tarihi", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Column(name = "last_login_at")
    @Schema(description = "Son giriş yapılan tarih", example = "2024-12-02T14:45:00")
    private LocalDateTime lastLoginAt;
    
    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Kullanıcının gönderdiği mesajlar", hidden = true)
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "triggeredBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Kullanıcının tetiklediği balance olayları", hidden = true)
    private List<BalanceEvent> balanceEvents = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "Kullanıcının oluşturduğu topluluk kuralları", hidden = true)
    private List<CommunityRule> createdRules = new ArrayList<>();

    @Schema(description = "Kullanıcı rolleri - Sistem içindeki yetki seviyelerini belirler")
    public enum Role {
        @Schema(description = "Normal kullanıcı - Temel işlemleri yapabilir")
        USER,
        @Schema(description = "Moderatör - Mesaj ve kural moderasyonu yapabilir")
        MODERATOR,
        @Schema(description = "Yönetici - Tüm sistem işlemlerini yapabilir")
        ADMIN
    }
}