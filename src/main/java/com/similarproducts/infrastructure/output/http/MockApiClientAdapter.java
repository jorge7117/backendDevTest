package com.similarproducts.infrastructure.output.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.similarproducts.domain.exception.ProductNotFoundException;
import com.similarproducts.domain.model.Product;
import com.similarproducts.domain.port.out.ProductDetailsPort;
import com.similarproducts.domain.port.out.SimilarProductsPort;

import java.util.List;
import java.util.Optional;

/**
 * HTTP Output Adapter connecting to external mock APIs running on port 3001
 */
@Component
public class MockApiClientAdapter implements SimilarProductsPort, ProductDetailsPort {

    private final RestTemplate restTemplate;
    private final String mockApiBaseUrl;

    public MockApiClientAdapter(
            RestTemplate restTemplate,
            @Value("${mock.api.port:3001}") String mockApiPort,
            @Value("${mock.api.base-url:http://localhost}") String mockApiBaseUrl) {
        this.restTemplate = restTemplate;
        this.mockApiBaseUrl = mockApiBaseUrl + ":" + mockApiPort;
    }


    @Override
    @Cacheable(value = "similarIds", key = "#productId", unless = "#result == null || #result.isEmpty()")
    public List<String> getSimilarProductIds(String productId) {
        String url = String.format("%s/product/%s/similarids", mockApiBaseUrl, productId);
        try {
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException(productId);
        } catch (Exception ex) {
            return List.of();
        }
    }

    @Override
    @Cacheable(value = "productDetails", key = "#productId", unless = "#result.isEmpty()")
    public Optional<Product> getProductDetail(String productId) {
        String url = String.format("%s/product/%s", mockApiBaseUrl, productId);
        try {
            ProductDto dto = restTemplate.getForObject(url, ProductDto.class);
            return Optional.ofNullable(dto).map(ProductDto::toDomain);
        } catch (Exception ex) {
            // Return empty optional on 404, timeout, or external errors
            return Optional.empty();
        }
    }
}