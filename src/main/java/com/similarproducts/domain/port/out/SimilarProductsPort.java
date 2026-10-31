package com.similarproducts.domain.port.out;

import java.util.List;

/**
 * Secondary Output Port for querying the list of similar product IDs
 */
public interface SimilarProductsPort {

    /**
     * Retrieves the list of similar product IDs from an external datasource
     *
     * @param productId Target product ID
     * @return List of similar product ID strings
     */
    List<String> getSimilarProductIds(String productId);
}