package com.enviro.assessment.junior.adrianmajavu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @RestControllerAdvice makes this class intercept exceptions thrown from
 * ANY @RestController in the app - one place to catch them, instead of
 * try/catch blocks repeated in every controller method. This is the
 * "global exception handling" advanced requirement.
 *
 * Each @ExceptionHandler method below "listens" for one exception type
 * and converts it into a proper HTTP status + a consistent ErrorResponse
 * body, so the React frontend never sees a raw stack trace or a 500 for
 * something that's really a validation problem.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse body = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(InvalidWithdrawalException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWithdrawal(InvalidWithdrawalException ex) {
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Triggered automatically when @Valid on a controller method finds a
    // DTO field that fails its validation annotation (e.g. amount is null).
    // We pull the field-level messages out and join them into one string.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorResponse body = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Catch-all safety net: anything unexpected (e.g. a NullPointerException
    // from a bug) still comes back as a clean JSON error instead of Spring's
    // default HTML error page or an unhandled stack trace leaking to the client.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
