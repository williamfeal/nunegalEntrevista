package com.example.similarproducts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.similarproducts.application.exception.DownstreamServiceException;
import com.example.similarproducts.application.exception.ProductNotFoundException;
import com.example.similarproducts.domain.ProductDetail;
import com.example.similarproducts.infrastructure.client.CatalogSimilarProductsClient;
import com.example.similarproducts.infrastructure.client.SimilarProductsClient;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

class CatalogSimilarProductsClientTest {

    private MockWebServer server;
    private SimilarProductsClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(server.url("/").toString())
                .build();
        client = new CatalogSimilarProductsClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void fetchSimilarProductIdsReturnsOrderedList() {
        server.enqueue(new MockResponse()
                .setBody("[\"1\",\"2\",\"3\"]")
                .setHeader("Content-Type", "application/json"));

        Mono<List<String>> result = client.fetchSimilarProductIds("10");

        assertThat(result.block()).containsExactly("1", "2", "3");
    }

    @Test
    void fetchSimilarProductIdsPropagatesNotFound() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client.fetchSimilarProductIds("10").block())
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void fetchSimilarProductIdsPropagatesServerErrors() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.fetchSimilarProductIds("10").block())
                .isInstanceOf(DownstreamServiceException.class);
    }

    @Test
    void fetchProductDetailReturnsDomain() {
        server.enqueue(new MockResponse()
                .setBody("{\"id\":\"1\",\"name\":\"Product\",\"price\":12.3,\"availability\":true}")
                .setHeader("Content-Type", "application/json"));

        ProductDetail detail = client.fetchProductDetail("1").block();

        assertThat(detail)
                .isNotNull()
                .extracting(ProductDetail::id, ProductDetail::name, ProductDetail::price, ProductDetail::availability)
                .containsExactly("1", "Product", new BigDecimal("12.3"), true);
    }

    @Test
    void fetchProductDetailPropagatesNotFound() {
        server.enqueue(new MockResponse().setResponseCode(404));

        assertThatThrownBy(() -> client.fetchProductDetail("2").block())
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void fetchProductDetailPropagatesServerErrors() {
        server.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> client.fetchProductDetail("2").block())
                .isInstanceOf(DownstreamServiceException.class);
    }
}
