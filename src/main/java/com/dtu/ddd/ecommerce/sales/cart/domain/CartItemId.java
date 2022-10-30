package com.dtu.ddd.ecommerce.sales.cart.domain;

import java.util.UUID;

public record CartItemId(UUID id) {
  public static CartItemId generate() {
    return new CartItemId(UUID.randomUUID());
  }
}
