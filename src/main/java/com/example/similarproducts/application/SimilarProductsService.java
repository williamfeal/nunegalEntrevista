package com.example.similarproducts.application;

import com.example.similarproducts.application.exception.ProductNotFoundException;
import com.example.similarproducts.domain.ProductDetail;
import com.example.similarproducts.infrastructure.client.SimilarProductsClient;
import java.util.List;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SimilarProductsService {

    private static final int DETAIL_FETCH_CONCURRENCY = 4;

    private final SimilarProductsClient client;

    public SimilarProductsService(SimilarProductsClient client) {
        this.client = client;
    }

    public Mono<List<ProductDetail>> fetchSimilarProducts(String productId) {
        return client.fetchSimilarProductIds(productId)
                .flatMapMany(ids -> Flux.fromIterable(ids)
                        .flatMapSequential(client::fetchProductDetail, DETAIL_FETCH_CONCURRENCY, DETAIL_FETCH_CONCURRENCY))
                .collectList()
                .map(List::copyOf)
                .onErrorMap(ProductNotFoundException.class, ex -> ex);
    }
}
