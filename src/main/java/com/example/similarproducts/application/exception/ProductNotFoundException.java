package com.example.similarproducts.application.exception;

/**
 * Signals that the requested product could not be found in the downstream catalog service.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super("Product " + productId + " was not found");
    }
}
