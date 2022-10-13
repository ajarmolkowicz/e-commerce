package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductEvents;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import lombok.Value;

public interface CartEvents extends DomainEvent {
  @Value
  class ProductAddedToCart implements ProductEvents {
    UUID eventId;
    Instant when;
    CartId cartId;
    ProductId productId;
    Quantity quantity;

    public ProductAddedToCart(CartId cartId, ProductId productId, Quantity quantity) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.cartId = cartId;
      this.productId = productId;
      this.quantity = quantity;
    }
  }

  @Value
  class ProductDeletedFromCart implements ProductEvents {
    UUID eventId;
    Instant when;
    CartId cartId;
    ProductId productId;

    public ProductDeletedFromCart(CartId cartId, ProductId productId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.cartId = cartId;
      this.productId = productId;
    }
  }
}
