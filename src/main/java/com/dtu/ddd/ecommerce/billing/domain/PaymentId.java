package com.dtu.ddd.ecommerce.billing.domain;

import java.util.UUID;
import javax.validation.constraints.NotNull;

public record PaymentId(@NotNull UUID id) {
  public static PaymentId generate() {
    return new PaymentId(UUID.randomUUID());
  }
  public static PaymentId of(UUID id) {
    return new PaymentId(id);
  }

}
