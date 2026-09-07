package com.enviro.assessment.junior.adrianmajavu.exception;

/**
 * Thrown when a lookup (e.g. findById) finds nothing. Extends
 * RuntimeException so it's unchecked - callers up the stack aren't forced
 * to catch it everywhere; GlobalExceptionHandler catches it in one place
 * and turns it into a 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
