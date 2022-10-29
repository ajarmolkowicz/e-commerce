package com.dtu.ddd.ecommerce.sales.product.application.command;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import lombok.Getter;

import java.util.UUID;

public final class DeleteProductCommand {
  @Getter private final ProductId productId;

  public DeleteProductCommand(UUID productId) {
    this.productId = new ProductId(productId);
  }
}
