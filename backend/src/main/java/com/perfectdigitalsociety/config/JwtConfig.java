package com.perfectdigitalsociety.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Data
public class JwtConfig {
    
    private String secret = "perfectDigitalSocietySecretKey2024! @#$%^&*()_+{}|:<>?[]\\;',./`~";
    private int expiration = 86400; // 24 hours in seconds
    private int refreshExpiration = 604800; // 7 days in seconds
    private String header = "Authorization";
    private String prefix = "Bearer ";
    private String issuer = "Perfect Digital Society";
}