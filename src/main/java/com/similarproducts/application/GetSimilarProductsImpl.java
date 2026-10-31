package com.similarproducts.application;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.similarproducts.domain.exception.ProductNotFoundException;
import com.similarproducts.domain.model.Product;
import com.similarproducts.domain.port.in.*;
import com.similarproducts.domain.port.out.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Application service orchestrating the retrieval of similar product details concurrently
 */
@Service
public class GetSimilarProductsImpl implements GetSimilarProductsIn {

    private final SimilarProductsPort similarProductsPort;
    private final ProductDetailsPort productDetailsPort;
    private final Executor taskExecutor;

    /**
     * Constructor injection for required output ports and dedicated thread pool
     *
     * @param similarProductsPort Port to fetch similar product IDs
     * @param productDetailsPort  Port to fetch individual product details
     * @param taskExecutor        Custom ThreadPoolExecutor to prevent commonPool starvation
     */
    public GetSimilarProductsImpl(
            SimilarProductsPort similarProductsPort,
            ProductDetailsPort productDetailsPort,
            @Qualifier("taskExecutor") Executor taskExecutor) {
        this.similarProductsPort = similarProductsPort;
        this.productDetailsPort = productDetailsPort;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Executes the similar products orchestration pipeline
     *
     * @param productId Target product ID
     * @return List of matching Product domain entities
     * @throws ProductNotFoundException if the target product or its similarity list is missing
     */
    @Override
    public List<Product> execute(String productId) {
        // Step 1: Retrieve similar product IDs from external port
        List<String> similarIds = similarProductsPort.getSimilarProductIds(productId);

        if (similarIds == null || similarIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Step 2: Trigger asynchronous fetch tasks for each product ID
        List<CompletableFuture<Optional<Product>>> futures = similarIds.stream()
                .map(id -> CompletableFuture
                        .supplyAsync(() -> productDetailsPort.getProductDetail(id), taskExecutor)
                        .exceptionally(ex -> Optional.empty()) // Fault tolerance: ignore individual failures
                )
                .toList();

        // Step 3: Barrier block waiting for all asynchronous tasks to finish
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        allFutures.join();

        // Step 4: Aggregate results and filter out empty Optionals (failed/missing products)
        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(Optional::stream)
                .toList();
    }

    // Getters for integration testing verification
    public SimilarProductsPort getSimilarProductsPort() {
        return similarProductsPort;
    }

    public ProductDetailsPort getProductDetailsPort() {
        return productDetailsPort;
    }

    public Executor getTaskExecutor() {
        return taskExecutor;
    }
}