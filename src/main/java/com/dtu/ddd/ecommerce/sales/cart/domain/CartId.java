package com.dtu.ddd.ecommerce.sales.cart.domain;

import java.util.UUID;

public record CartId(UUID id) {
  public static CartId generate() {
    return new CartId(UUID.randomUUID());
  }
}
