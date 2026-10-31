package com.similarproducts.infrastructure.output.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.similarproducts.domain.model.Product;

import java.math.BigDecimal;

/**
 * Infrastructure DTO mapping JSON response from mock server (port 3001)
 */
public record ProductDto(
    @JsonProperty("id") String id,
    @JsonProperty("name") String name,
    @JsonProperty("price") BigDecimal price,
    @JsonProperty("availability") Boolean availability
) {
    /**
     * Converts infrastructure DTO into pure Domain entity
     */
    public Product toDomain() {
        return new Product(id, name, price, availability);
    }
}