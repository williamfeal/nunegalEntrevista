package com.example.similarproducts.application.exception;

/**
 * Indicates a non-functional error while talking to the downstream services (e.g., networking or server issues).
 */
public class DownstreamServiceException extends RuntimeException {
    public DownstreamServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownstreamServiceException(String message) {
        super(message);
    }
}
