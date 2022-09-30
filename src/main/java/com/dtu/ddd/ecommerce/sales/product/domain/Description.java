package com.dtu.ddd.ecommerce.sales.product.domain;

import javax.validation.constraints.NotEmpty;

public record Description(@NotEmpty String description) {
  public Description {
    if (description == null || description.strip().isBlank()) {
      throw new IllegalArgumentException("Product description cannot be blank");
    }
  }
}
