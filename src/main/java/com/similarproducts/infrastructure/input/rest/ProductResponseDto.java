package com.similarproducts.infrastructure.input.rest;

import java.math.BigDecimal;

import com.similarproducts.domain.model.Product;

/**
 * REST API response DTO exposed on port 5000
 */
public record ProductResponseDto(
    String id,
    String name,
    BigDecimal price,
    Boolean availability
) {
    public static ProductResponseDto fromDomain(Product product) {
        return new ProductResponseDto(
            product.id(),
            product.name(),
            product.price(),
            product.availability()
        );
    }
}