package com.similarproducts.infrastructure.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.similarproducts.domain.port.in.GetSimilarProductsIn;

import java.util.List;

/**
 * REST Input Adapter exposing required endpoint on port 5000.
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private final GetSimilarProductsIn getSimilarProductsUseCase;

    public ProductController(GetSimilarProductsIn getSimilarProductsUseCase) {
        this.getSimilarProductsUseCase = getSimilarProductsUseCase;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductResponseDto>> getSimilarProducts(@PathVariable String productId) {
        List<ProductResponseDto> response = getSimilarProductsUseCase.execute(productId)
                .stream()
                .map(ProductResponseDto::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }
}