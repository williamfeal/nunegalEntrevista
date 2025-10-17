package com.example.similarproducts.domain;

import java.math.BigDecimal;

/**
 * Immutable representation of a product detail as provided by the downstream catalog service.
 */
public record ProductDetail(String id, String name, BigDecimal price, boolean availability) {
}
