package com.example.similarproducts.infrastructure.http;

import com.example.similarproducts.application.exception.DownstreamServiceException;
import com.example.similarproducts.application.exception.ProductNotFoundException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<Map<String, String>> handleProductNotFound(ProductNotFoundException exception) {
        return Mono.just(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(DownstreamServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public Mono<Map<String, String>> handleDownstreamError(DownstreamServiceException exception) {
        logger.warn("Downstream catalog error", exception);
        return Mono.just(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<Map<String, String>> handleUnexpected(Throwable throwable) {
        logger.error("Unexpected error", throwable);
        return Mono.just(Map.of("message", "Unexpected error"));
    }
}
