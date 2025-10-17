package com.example.similarproducts.controller;

import com.example.similarproducts.application.SimilarProductsService;
import com.example.similarproducts.domain.ProductDetail;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class SimilarProductsController {

    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/{productId}/similar")
    public Mono<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        return similarProductsService.fetchSimilarProducts(productId);
    }
}
