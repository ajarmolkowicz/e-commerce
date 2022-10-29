package com.dtu.ddd.ecommerce.sales.cart.domain;

import java.util.UUID;

public record ClientId(UUID id) {
  public static ClientId generate() {
    return new ClientId(UUID.randomUUID());
  }

  public static ClientId fromString(String id) {
    return new ClientId(UUID.fromString(id));
  }
}
