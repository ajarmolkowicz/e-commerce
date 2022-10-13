package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductEvents;
import java.time.Instant;
import java.util.UUID;
import lombok.Value;

public interface OrderEvents {
  @Value
  class OrderSubmitted implements ProductEvents {
    UUID eventId;
    Instant when;
    OrderId orderId;
    CartId cartId;

    public OrderSubmitted(OrderId orderId, CartId cartId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.orderId = orderId;
      this.cartId = cartId;
    }
  }

  @Value
  class OrderSubmissionFailed implements ProductEvents {
    UUID eventId;
    Instant when;
    CartId cartId;

    public OrderSubmissionFailed(CartId cartId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.cartId = cartId;
    }
  }
}
