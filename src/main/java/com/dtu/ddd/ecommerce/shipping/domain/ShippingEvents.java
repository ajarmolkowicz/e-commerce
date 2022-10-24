package com.dtu.ddd.ecommerce.shipping.domain;

import com.dtu.ddd.ecommerce.shared.event.DomainEvent;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

public interface ShippingEvents extends DomainEvent {
  @Value
  class OrderShipped implements ShippingEvents {
    UUID eventId;
    Instant when;
    OrderId orderId;

    public OrderShipped(OrderId orderId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.orderId = orderId;
    }
  }
}
