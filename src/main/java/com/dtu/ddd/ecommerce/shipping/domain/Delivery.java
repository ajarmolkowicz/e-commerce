package com.dtu.ddd.ecommerce.shipping.domain;

import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;

@AggregateRoot @Entity
public class Delivery {
  @Getter private DeliveryId id;
  @Getter private OrderId orderId;
  @Getter private Address address;
  @Getter private State state;
  @Getter private Version version;

  public Delivery(OrderId orderId, Address address) {
    this.id = DeliveryId.generate();
    this.orderId = orderId;
    this.address = address;
    this.state = State.INITIALIZED;
  }

  public Delivery(DeliveryId id, OrderId orderId, Address address, State state, Version version) {
    this.id = id;
    this.orderId = orderId;
    this.address = address;
    this.state = state;
    this.version = version;
  }

  public void dispatch() {
    this.state = State.IN_DELIVERY;
  }

    public enum State {
    INITIALIZED, IN_DELIVERY, DELIVERED
  }
}
