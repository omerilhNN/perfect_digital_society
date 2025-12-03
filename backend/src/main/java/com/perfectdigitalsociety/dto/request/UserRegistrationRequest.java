package com.perfectdigitalsociety.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kullanıcı kayıt formu için gerekli bilgiler")
public class UserRegistrationRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(
        description = "Benzersiz kullanıcı adı",
        example = "john_doe",
        minLength = 3,
        maxLength = 50,
        required = true
    )
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Schema(
        description = "Geçerli e-posta adresi",
        example = "john.doe@example.com",
        format = "email",
        maxLength = 100,
        required = true
    )
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    @Schema(
        description = "Güçlü şifre (minimum 8 karakter)",
        example = "SecurePassword123!",
        minLength = 8,
        maxLength = 255,
        required = true
    )
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Schema(
        description = "Kullanıcının adı",
        example = "John",
        maxLength = 50,
        required = true
    )
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Schema(
        description = "Kullanıcının soyadı",
        example = "Doe",
        maxLength = 50,
        required = true
    )
    private String lastName;
}