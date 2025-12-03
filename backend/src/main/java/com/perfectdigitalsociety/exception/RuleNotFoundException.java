package com.perfectdigitalsociety.exception;

public class RuleNotFoundException extends RuntimeException {
    
    public RuleNotFoundException(String message) {
        super(message);
    }
    
    public RuleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}