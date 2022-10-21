package com.dtu.ddd.ecommerce.billing.domain;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record ReferenceId(@NotNull UUID id) {
  public static ReferenceId of(UUID id) {
    return new ReferenceId(id);
  }
}
