package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductEvents;
import java.time.Instant;
import java.util.UUID;

import com.dtu.ddd.ecommerce.shared.event.DomainEvent;
import lombok.Value;
import org.joda.money.Money;

public interface OrderEvents extends DomainEvent {
  @Value
  class OrderSubmitted implements OrderEvents {
    UUID eventId;
    Instant when;
    OrderId orderId;
    CartId cartId;
    Money total;

    public OrderSubmitted(OrderId orderId, CartId cartId, Money total) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.orderId = orderId;
      this.cartId = cartId;
      this.total = total;
    }
  }

  @Value
  class OrderSubmissionFailed implements OrderEvents {
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
