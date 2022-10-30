package com.dtu.ddd.ecommerce.sales.product.application.command;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import lombok.Getter;

import java.util.UUID;
import org.jmolecules.architecture.cqrs.annotation.Command;

@Command
public final class EditProductQuantityCommand {
  @Getter private final ProductId productId;
  @Getter private final Quantity quantity;

  public EditProductQuantityCommand(UUID productId, Integer quantity) {
    this.productId = new ProductId(productId);
    this.quantity = new Quantity(quantity);
  }
}
