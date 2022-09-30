package com.dtu.ddd.ecommerce.sales.product.domain;

import java.util.UUID;
import javax.validation.constraints.NotNull;

public record ProductId(@NotNull UUID id) {
  public static ProductId generate() {
    return new ProductId(UUID.randomUUID());
  }
}
