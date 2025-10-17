package com.example.similarproducts.infrastructure.client;

import com.example.similarproducts.application.exception.DownstreamServiceException;
import com.example.similarproducts.application.exception.ProductNotFoundException;
import com.example.similarproducts.domain.ProductDetail;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;

@Component
public class CatalogSimilarProductsClient implements SimilarProductsClient {

    private final WebClient webClient;

    public CatalogSimilarProductsClient(WebClient catalogWebClient) {
        this.webClient = catalogWebClient;
    }

    @Override
    public Mono<List<String>> fetchSimilarProductIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), response ->
                        Mono.error(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.createException().flatMap(ex -> Mono.error(new DownstreamServiceException("Client error while fetching similar ids", ex))))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new DownstreamServiceException("Error fetching similar ids")))
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .map(List::copyOf)
                .onErrorMap(WebClientRequestException.class, ex ->
                        new DownstreamServiceException("Error communicating with catalog service", ex))
                .onErrorMap(WebClientResponseException.Forbidden.class, ex ->
                        new DownstreamServiceException("Catalog service forbids access", ex));
    }

    @Override
    public Mono<ProductDetail> fetchProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND), response ->
                        Mono.error(new ProductNotFoundException(productId)))
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.createException().flatMap(ex -> Mono.error(new DownstreamServiceException("Client error while fetching product detail", ex))))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new DownstreamServiceException("Error fetching product detail")))
                .bodyToMono(ProductDetail.class)
                .onErrorMap(WebClientRequestException.class, ex ->
                        new DownstreamServiceException("Error communicating with catalog service", ex));
    }
}
