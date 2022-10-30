package com.dtu.ddd.ecommerce.sales.cart.application.command;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import lombok.Getter;

import java.util.UUID;
import org.jmolecules.architecture.cqrs.annotation.Command;

@Command
public final class AddProductToCartCommand {
  @Getter private final CartId cartId;
  @Getter private final ProductId productId;
  @Getter private final Quantity quantity;

  public AddProductToCartCommand(UUID cartId, UUID productId, Integer quantity) {
    this.cartId = new CartId(cartId);
    this.productId = new ProductId(productId);
    this.quantity = new Quantity(quantity);
  }
}
