package com.perfectdigitalsociety.mapper;

import com.perfectdigitalsociety.dto.response.AuthResponse;
import com.perfectdigitalsociety.dto.response.UserProfileResponse;
import com.perfectdigitalsociety.dto.response.UserResponse;
import com.perfectdigitalsociety.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    
    /**
     * Convert User entity to UserResponse DTO
     */
    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserResponse toUserResponse(User user);
    
    /**
     * Convert User entity to UserProfileResponse DTO
     */
    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserProfileResponse toUserProfileResponse(User user);
    
    /**
     * Convert list of User entities to list of UserResponse DTOs
     */
    List<UserResponse> toUserResponseList(List<User> users);
    
    /**
     * Convert list of User entities to list of UserProfileResponse DTOs
     */
    List<UserProfileResponse> toUserProfileResponseList(List<User> users);
    
    /**
     * Create AuthResponse with user and token information
     */
    @Mapping(target = "user", source = "user")
    @Mapping(target = "token", source = "token")
    @Mapping(target = "expiresAt", source = "expiresAt")
    AuthResponse toAuthResponse(User user, String token, LocalDateTime expiresAt);
    
    /**
     * Convert User role enum to string
     */
    default String mapRole(User.Role role) {
        return role != null ? role.toString() : null;
    }
    
    /**
     * Convert string to User role enum
     */
    default User.Role mapRole(String role) {
        return role != null ? User.Role.valueOf(role.toUpperCase()) : null;
    }

    /**
     * Convert UserProfileResponse DTO to UserResponse
     */
    default UserResponse profileToUserResponse(UserProfileResponse profile) {
        if (profile == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(profile.getId());
        response.setUsername(profile.getUsername());
        response.setEmail(profile.getEmail());
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setRole(profile.getRole());
        response.setFreedomScore(profile.getFreedomScore());
        response.setSecurityScore(profile.getSecurityScore());
        response.setReputationScore(profile.getReputationScore());
        response.setIsActive(profile.getIsActive());
        response.setCreatedAt(profile.getCreatedAt());
        response.setLastLoginAt(profile.getLastLoginAt());

        return response;
    }
}