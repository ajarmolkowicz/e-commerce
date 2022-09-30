package com.dtu.ddd.ecommerce.delivery.domain;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;

@AggregateRoot @Entity
public class Delivery {
  private DeliveryId id;
  private OrderId orderId;
  private Address address;

  void dispatch() {
    throw new UnsupportedOperationException();
  }
}
