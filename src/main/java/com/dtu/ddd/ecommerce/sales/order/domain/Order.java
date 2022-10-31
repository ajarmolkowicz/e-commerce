package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;
import org.joda.money.Money;

import static org.valid4j.Assertive.require;

@AggregateRoot
@Entity
public class Order {
  @Identity @Getter private final OrderId id;
  private final Set<OrderItem> items;
  @Getter private final SubmissionTime submissionTime;
  @Getter private ShippingTime shippingTime;
  @Getter private Version version;

  public Order(Set<OrderItem> items) {
    this.id = OrderId.generate();
    require(!items.isEmpty(), "order items cannot be empty");
    this.items = items;
    this.submissionTime = new SubmissionTime(Instant.now());
  }

  public Order(OrderId id, Set<OrderItem> items, SubmissionTime submissionTime, ShippingTime shippingTime, Version version) {
    this.id = id;
    this.items = items;
    this.submissionTime = submissionTime;
    this.shippingTime = shippingTime;
    this.version = version;
  }

  public Set<OrderItem> getItems() {
    return Collections.unmodifiableSet(items);
  }

  public Money total() {
    return Money.total(items.stream()
        .map($ -> $.money().multipliedBy($.quantity().value()))
        .collect(Collectors.toSet()));
  }

  public void shipped(ShippingTime time) {
    this.shippingTime = time;
  }
}
