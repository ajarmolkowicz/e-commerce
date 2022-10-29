package com.dtu.ddd.ecommerce.sales.order.domain;

import java.util.UUID;
import javax.validation.constraints.NotNull;

public record OrderId(@NotNull UUID id) {
  public static OrderId generate() {
    return new OrderId(UUID.randomUUID());
  }

  public static OrderId of(UUID id) {
    return new OrderId(id);
  }
}
