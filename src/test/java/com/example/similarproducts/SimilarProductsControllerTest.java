package com.example.similarproducts;

import static org.mockito.Mockito.when;

import com.example.similarproducts.application.SimilarProductsService;
import com.example.similarproducts.application.exception.DownstreamServiceException;
import com.example.similarproducts.application.exception.ProductNotFoundException;
import com.example.similarproducts.domain.ProductDetail;
import com.example.similarproducts.infrastructure.http.RestExceptionHandler;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.similarproducts.controller.SimilarProductsController;

@WebFluxTest(controllers = SimilarProductsController.class)
@Import(RestExceptionHandler.class)
class SimilarProductsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SimilarProductsService similarProductsService;

    @Test
    void returnsSimilarProducts() {
        when(similarProductsService.fetchSimilarProducts("10"))
                .thenReturn(reactor.core.publisher.Mono.just(List.of(
                        new ProductDetail("1", "One", new BigDecimal("10.00"), true))));

        webTestClient.get()
                .uri("/product/10/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[0].name").isEqualTo("One")
                .jsonPath("$[0].price").isEqualTo(10.00)
                .jsonPath("$[0].availability").isEqualTo(true);
    }

    @Test
    void returnsNotFoundWhenServiceThrowsProductNotFound() {
        when(similarProductsService.fetchSimilarProducts("10"))
                .thenReturn(reactor.core.publisher.Mono.error(new ProductNotFoundException("10")));

        webTestClient.get()
                .uri("/product/10/similar")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void returnsBadGatewayWhenServiceThrowsDownstreamError() {
        when(similarProductsService.fetchSimilarProducts("10"))
                .thenReturn(reactor.core.publisher.Mono.error(new DownstreamServiceException("Boom")));

        webTestClient.get()
                .uri("/product/10/similar")
                .exchange()
                .expectStatus().isEqualTo(502);
    }
}
