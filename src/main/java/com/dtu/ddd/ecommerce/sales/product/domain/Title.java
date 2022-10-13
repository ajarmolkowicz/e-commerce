package com.dtu.ddd.ecommerce.sales.product.domain;

public record Title(String title) {
  public Title {
    if (title == null || title.strip().isBlank()) {
      throw new IllegalArgumentException("Product title cannot be blank");
    }
  }
}