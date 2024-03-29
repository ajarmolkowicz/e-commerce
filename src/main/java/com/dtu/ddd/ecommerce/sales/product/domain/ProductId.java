package com.dtu.ddd.ecommerce.sales.product.domain;

import java.util.UUID;

public record ProductId(UUID id) {
  public static ProductId generate() {
    return new ProductId(UUID.randomUUID());
  }

  public static ProductId fromString(String id) {
    return new ProductId(UUID.fromString(id));
  }
}
