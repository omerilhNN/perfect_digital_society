package com.perfectdigitalsociety.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.stream.Stream;

public class SecurityUtils {
    
    private SecurityUtils() {
        // Utility class
    }
    
    /**
     * Get current authenticated user's username
     */
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || ! authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        } else if (principal instanceof String) {
            return Optional.of((String) principal);
        }
        
        return Optional.empty();
    }
    
    /**
     * Get current authenticated user details
     */
    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || ! authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserPrincipal) {
            return Optional.of((UserPrincipal) principal);
        }
        
        return Optional.empty();
    }
    
    /**
     * Check if current user has specific role
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + role));
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public static boolean hasAnyRole(String... roles) {
        return Stream.of(roles).anyMatch(SecurityUtils::hasRole);
    }

    /**
     * Check if current user is admin
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user is moderator
     */
    public static boolean isModerator() {
        return hasRole("MODERATOR");
    }

    /**
     * Check if current user has admin or moderator privileges
     */
    public static boolean hasModeratorPrivileges() {
        return hasAnyRole("ADMIN", "MODERATOR");
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               authentication.isAuthenticated() &&
               !"anonymousUser".equals(authentication.getName());
    }
}