package com.dtu.ddd.ecommerce.billing.domain;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;
import org.joda.money.Money;

@AggregateRoot @Entity
public class Payment {
  private PaymentId paymentId;
  private OrderId orderId;
  private Money total;

  void collect() {
    throw new UnsupportedOperationException();
  }
}
