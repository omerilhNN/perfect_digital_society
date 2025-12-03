package com.perfectdigitalsociety.exception;

import com.perfectdigitalsociety.dto.response.StatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle user not found exception
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<StatusResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        
        log.error("User not found: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("User not found: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * Handle user already exists exception
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<StatusResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {
        
        log.error("User already exists: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("User already exists: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    /**
     * Handle message not found exception
     */
    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<StatusResponse> handleMessageNotFoundException(
            MessageNotFoundException ex, WebRequest request) {
        
        log.error("Message not found: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Message not found: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * Handle rule not found exception
     */
    @ExceptionHandler(RuleNotFoundException.class)
    public ResponseEntity<StatusResponse> handleRuleNotFoundException(
            RuleNotFoundException ex, WebRequest request) {
        
        log.error("Rule not found: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Community rule not found: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    /**
     * Handle unauthorized access exception
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StatusResponse> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        log.error("Unauthorized access: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Unauthorized access: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    /**
     * Handle balance calculation exception
     */
    @ExceptionHandler(BalanceCalculationException.class)
    public ResponseEntity<StatusResponse> handleBalanceCalculationException(
            BalanceCalculationException ex, WebRequest request) {
        
        log.error("Balance calculation error: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Balance calculation error: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Handle authentication exception
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<StatusResponse> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        log.error("Authentication failed: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Authentication failed: Invalid credentials");
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    /**
     * Handle bad credentials exception
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StatusResponse> handleBadCredentialsException(
            BadCredentialsException ex, WebRequest request) {
        
        log.error("Bad credentials: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Invalid username or password");
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    /**
     * Handle access denied exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StatusResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        log.error("Access denied: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Access denied: Insufficient privileges");
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    /**
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("success", false);
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());
        
        log.error("Validation failed: {}", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle illegal argument exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StatusResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        log.error("Illegal argument: {}", ex.getMessage());
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Invalid argument: " + ex.getMessage());
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Handle generic runtime exception
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StatusResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("An unexpected error occurred");
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Handle generic exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StatusResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        
        StatusResponse response = new StatusResponse();
        response.setSuccess(false);
        response.setMessage("Internal server error occurred");
        response.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}