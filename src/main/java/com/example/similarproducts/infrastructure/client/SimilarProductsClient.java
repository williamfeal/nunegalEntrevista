package com.example.similarproducts.infrastructure.client;

import com.example.similarproducts.domain.ProductDetail;
import java.util.List;
import reactor.core.publisher.Mono;

public interface SimilarProductsClient {

    Mono<List<String>> fetchSimilarProductIds(String productId);

    Mono<ProductDetail> fetchProductDetail(String productId);
}
