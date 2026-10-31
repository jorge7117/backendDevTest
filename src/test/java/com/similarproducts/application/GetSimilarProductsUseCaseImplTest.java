package com.similarproducts.application;

import com.similarproducts.domain.model.Product;
import com.similarproducts.domain.port.out.ProductDetailsPort;
import com.similarproducts.domain.port.out.SimilarProductsPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsUseCaseImplTest {

    @Mock
    private SimilarProductsPort similarProductsPort;

    @Mock
    private ProductDetailsPort productDetailsPort;

    private GetSimilarProductsImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetSimilarProductsImpl(
                similarProductsPort,
                productDetailsPort,
                Executors.newFixedThreadPool(4)
        );
    }

    @Test
    @DisplayName("Should return similar products successfully")
    void shouldReturnSimilarProductsSuccessfully() {
        // Given
        String targetId = "1";
        when(similarProductsPort.getSimilarProductIds(targetId)).thenReturn(List.of("2", "3"));

        Product p2 = new Product("2", "Dress", new BigDecimal("19.99"), true);
        Product p3 = new Product("3", "Pants", new BigDecimal("29.99"), true);

        when(productDetailsPort.getProductDetail("2")).thenReturn(Optional.of(p2));
        when(productDetailsPort.getProductDetail("3")).thenReturn(Optional.of(p3));

        // When
        List<Product> result = useCase.execute(targetId);

        // Then
        assertEquals(2, result.size());
        assertEquals("2", result.get(0).id());
        assertEquals("3", result.get(1).id());
        verify(similarProductsPort, times(1)).getSimilarProductIds(targetId);
    }

    @Test
    @DisplayName("Should handle partial failures gracefully without throwing exception")
    void shouldHandlePartialFailuresGracefully() {
        // Given
        String targetId = "1";
        when(similarProductsPort.getSimilarProductIds(targetId)).thenReturn(List.of("2", "3"));

        Product p2 = new Product("2", "Dress", new BigDecimal("19.99"), true);

        when(productDetailsPort.getProductDetail("2")).thenReturn(Optional.of(p2));
        when(productDetailsPort.getProductDetail("3")).thenReturn(Optional.empty()); // Failed detail

        // When
        List<Product> result = useCase.execute(targetId);

        // Then
        assertEquals(1, result.size());
        assertEquals("2", result.get(0).id());
    }
}