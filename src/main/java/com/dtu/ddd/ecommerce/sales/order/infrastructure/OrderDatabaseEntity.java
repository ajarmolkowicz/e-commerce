package com.dtu.ddd.ecommerce.sales.order.infrastructure;

import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.sales.order.domain.ShippingTime;
import com.dtu.ddd.ecommerce.sales.order.domain.SubmissionTime;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
class OrderDatabaseEntity {
  @Setter UUID order_id;
  @Setter Instant submission_time;
  @Setter Instant shipping_time;
  @Setter Integer version;

  Order toDomainModel(Collection<OrderItemDatabaseEntity> items) {
    return new Order(
        new OrderId(order_id),
        items.stream().map(OrderItemDatabaseEntity::toDomainModel).collect(Collectors.toSet()),
        new SubmissionTime(submission_time),
        Optional.ofNullable(shipping_time).map($ -> new ShippingTime(shipping_time)).orElse(null),
        new Version(version)
    );
  }
}
