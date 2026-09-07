package com.enviro.assessment.junior.adrianmajavu.exception;

import java.time.LocalDateTime;

/**
 * Every error the API returns has this same JSON shape, regardless of
 * which exception caused it. A consistent error contract means the React
 * frontend can write ONE error-handling code path instead of guessing
 * the shape per endpoint.
 */
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
