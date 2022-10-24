package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;

import java.time.Instant;
import java.util.UUID;

import com.dtu.ddd.ecommerce.shared.event.DomainEvent;
import com.dtu.ddd.ecommerce.shared.vo.Address;
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
    Address address;

    public OrderSubmitted(OrderId orderId, CartId cartId, Money total, Address address) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.orderId = orderId;
      this.cartId = cartId;
      this.total = total;
      this.address = address;
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
