package com.perfectdigitalsociety.exception;

public class BalanceCalculationException extends RuntimeException {
    
    public BalanceCalculationException(String message) {
        super(message);
    }
    
    public BalanceCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}