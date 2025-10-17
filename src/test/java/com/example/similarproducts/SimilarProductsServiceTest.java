package com.example.similarproducts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.similarproducts.application.SimilarProductsService;
import com.example.similarproducts.application.exception.ProductNotFoundException;
import com.example.similarproducts.domain.ProductDetail;
import com.example.similarproducts.infrastructure.client.SimilarProductsClient;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SimilarProductsServiceTest {

    private SimilarProductsClient client;
    private SimilarProductsService service;

    @BeforeEach
    void setUp() {
        client = Mockito.mock(SimilarProductsClient.class);
        service = new SimilarProductsService(client);
    }

    @Test
    void returnsDetailsInSameOrderAsSimilarIds() {
        when(client.fetchSimilarProductIds("10"))
                .thenReturn(Mono.just(List.of("5", "6")));
        when(client.fetchProductDetail("5"))
                .thenReturn(Mono.just(new ProductDetail("5", "First", new BigDecimal("10.00"), true)));
        when(client.fetchProductDetail("6"))
                .thenReturn(Mono.just(new ProductDetail("6", "Second", new BigDecimal("20.00"), false)));

        StepVerifier.create(service.fetchSimilarProducts("10"))
                .assertNext(result -> assertThat(result)
                        .extracting(ProductDetail::id)
                        .containsExactly("5", "6"))
                .verifyComplete();

        verify(client).fetchSimilarProductIds("10");
        verify(client).fetchProductDetail("5");
        verify(client).fetchProductDetail("6");
    }

    @Test
    void propagatesProductNotFoundFromClient() {
        when(client.fetchSimilarProductIds(anyString()))
                .thenReturn(Mono.error(new ProductNotFoundException("10")));

        StepVerifier.create(service.fetchSimilarProducts("10"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }
}
