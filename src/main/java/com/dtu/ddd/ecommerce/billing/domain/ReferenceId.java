package com.dtu.ddd.ecommerce.billing.domain;

import java.util.UUID;

public record ReferenceId(UUID id) {
  public static ReferenceId of(UUID id) {
    return new ReferenceId(id);
  }
}
