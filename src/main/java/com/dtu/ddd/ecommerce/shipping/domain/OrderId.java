package com.dtu.ddd.ecommerce.shipping.domain;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record OrderId(@NotNull UUID id) {
  public static OrderId generate() {
    return new OrderId(UUID.randomUUID());
  }
}
