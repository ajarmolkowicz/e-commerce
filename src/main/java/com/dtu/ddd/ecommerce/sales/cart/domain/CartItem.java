package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import org.jmolecules.ddd.annotation.Entity;

@Entity
public class CartItem {
  private CartItemId id;
  private ProductId productId;
  private Title title;
  private Quantity quantity;
}
