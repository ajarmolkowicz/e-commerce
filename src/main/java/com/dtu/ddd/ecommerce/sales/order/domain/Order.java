package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;
import org.joda.money.Money;

@AggregateRoot @Entity
public class Order {
  @Getter private OrderId id;
  @Getter private Set<OrderItem> items;
  @Getter private Money total;
  @Getter private SubmissionTime submissionTime;
  @Getter private ShippingTime shippingTime;
  @Getter private Version version;

  public Order(Set<OrderItem> items) {
    this.id = OrderId.generate();
    this.items = items;
    this.total = Money.total(items.stream()
        .map($ -> $.money().multipliedBy($.quantity().value()))
        .collect(Collectors.toSet()));
    this.submissionTime = new SubmissionTime(ZonedDateTime.now());
  }

  public Order(OrderId id, Set<OrderItem> items, Money total, SubmissionTime submissionTime, ShippingTime shippingTime, Version version) {
    this.id = id;
    this.items = items;
    this.total = total;
    this.submissionTime = submissionTime;
    this.shippingTime = shippingTime;
    this.version = version;
  }
}
