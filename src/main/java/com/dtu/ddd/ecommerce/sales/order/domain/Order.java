package com.dtu.ddd.ecommerce.sales.order.domain;

import java.time.ZonedDateTime;
import java.util.Set;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;
import org.joda.money.Money;

@AggregateRoot @Entity
public class Order {
  private OrderId id;
  private Set<OrderItem> items;
  private Money total;
  private OrderTime time;

  private void submit() {
    throw new UnsupportedOperationException();
  }

  private void onDelivered() {
    throw new UnsupportedOperationException();
  }

  private void onPaymentDeadlineMissed() {
    throw new UnsupportedOperationException();
  }

  record OrderTime (ZonedDateTime time) {}
}
