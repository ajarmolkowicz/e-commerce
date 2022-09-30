package com.dtu.ddd.ecommerce.sales.cart.domain;

import java.util.UUID;
import javax.validation.constraints.NotNull;

public record CartItemId(@NotNull UUID id) {
  public static CartItemId generate() {
    return new CartItemId(UUID.randomUUID());
  }
}
