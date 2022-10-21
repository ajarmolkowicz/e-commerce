package com.dtu.ddd.ecommerce.shipping.domain;

import java.util.UUID;
import javax.validation.constraints.NotNull;

public record DeliveryId(@NotNull UUID id) {
  public static DeliveryId generate() {
    return new DeliveryId(UUID.randomUUID());
  }
}
