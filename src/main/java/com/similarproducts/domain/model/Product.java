package com.similarproducts.domain.model;

import java.math.BigDecimal;

/**
 * Core domain entity representing a Product
 * Uses a Java Record to guarantee absolute immutability, thread-safety,
 * and zero boilerplate overhead during concurrent executions
 */
public record Product(
    String id,
    String name,
    BigDecimal price,
    Boolean availability
) {
    // Compact constructor for defensive invariant validation
    public Product {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be null or blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product price must be a non-negative value");
        }
    }
}