package com.similarproducts.domain.port.in;
import java.util.List;

import com.similarproducts.domain.model.Product;

/**
 * Primary Input Port for retrieving similar products
 */
public interface GetSimilarProductsIn {

    /**
     * Executes the business logic to fetch details of all products similar to the given productId
     *
     * @param productId Target product ID
     * @return List of matching Product domain entities
     * @throws com.inditex.similarproducts.domain.exception.ProductNotFoundException if the target product does not exist
     */
    List<Product> execute(String productId);
}