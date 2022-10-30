package com.dtu.ddd.ecommerce.shipping.domain;

import java.util.UUID;

public record DeliveryId(UUID id) {
  public static DeliveryId generate() {
    return new DeliveryId(UUID.randomUUID());
  }
}
