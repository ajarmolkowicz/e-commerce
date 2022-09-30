package com.dtu.ddd.ecommerce.sales.product.domain;

import javax.validation.constraints.NotEmpty;

public record Title(@NotEmpty String title) {
  public Title {
    if (title == null || title.strip().isBlank()) {
      throw new IllegalArgumentException("Product title cannot be blank");
    }
  }
}