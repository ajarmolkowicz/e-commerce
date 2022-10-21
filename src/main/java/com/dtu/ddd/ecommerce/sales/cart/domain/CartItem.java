package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import java.util.UUID;
import lombok.Getter;
import org.jmolecules.ddd.annotation.Entity;

@Entity
public class CartItem {
  @Getter private CartItemId id;
  @Getter private ProductId productId;
  @Getter private Quantity quantity;

  public CartItem(ProductId productId, Quantity quantity) {
    this.id = CartItemId.generate();
    this.productId = productId;
    this.quantity = quantity;
  }

  public CartItem(CartItemId id, ProductId productId, Quantity quantity) {
    this.id = id;
    this.productId = productId;
    this.quantity = quantity;
  }

  /* TODO : EDIT QUANTITY*/
}
