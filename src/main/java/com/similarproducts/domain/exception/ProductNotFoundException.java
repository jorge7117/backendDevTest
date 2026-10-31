package com.similarproducts.domain.exception;

/**
 * Exception thrown when a requested product or its similar product list is not found
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super("Product not found with ID: " + productId);
    }
}