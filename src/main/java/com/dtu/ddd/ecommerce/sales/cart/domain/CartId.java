package com.dtu.ddd.ecommerce.sales.cart.domain;

import java.util.UUID;
import javax.validation.constraints.NotNull;

public record CartId(@NotNull UUID id) {
  public static CartId generate() {
    return new CartId(UUID.randomUUID());
  }
}
