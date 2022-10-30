package com.dtu.ddd.ecommerce.billing.domain;

import java.util.UUID;

public record PaymentId(UUID id) {
  public static PaymentId generate() {
    return new PaymentId(UUID.randomUUID());
  }

  public static PaymentId of(UUID id) {
    return new PaymentId(id);
  }
}
