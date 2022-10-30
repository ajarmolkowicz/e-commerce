package com.dtu.ddd.ecommerce.shipping.domain;

import java.util.UUID;

public record OrderId(UUID id) {
  public static OrderId generate() {
    return new OrderId(UUID.randomUUID());
  }

  public static OrderId of(UUID id) {
    return new OrderId(id);
  }
}
