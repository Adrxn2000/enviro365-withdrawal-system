package com.enviro.assessment.junior.adrianmajavu.exception;

/**
 * Thrown when a withdrawal request fails one of the business rules
 * (age restriction, exceeds balance, exceeds 90% cap). Kept as one
 * exception type with a descriptive message, rather than one class per
 * rule, since all three cases map to the same HTTP response (400 Bad
 * Request) and the message alone is enough for the frontend to display.
 */
public class InvalidWithdrawalException extends RuntimeException {
    public InvalidWithdrawalException(String message) {
        super(message);
    }
}
