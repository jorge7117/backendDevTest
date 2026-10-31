package com.similarproducts.domain.port.out;

import java.util.Optional;

import com.similarproducts.domain.model.Product;

/**
 * Secondary Output Port for querying detailed information of an individual product
 */
public interface ProductDetailsPort {

    /**
     * Fetches detailed information for a single product by its ID
     * Returns an Optional to handle partial external failures gracefully without throwing global exceptions
     *
     * @param productId Product ID to fetch.
     * @return Optional containing the Product if successfully, or Optional.empty() on failure
     */
    Optional<Product> getProductDetail(String productId);
}