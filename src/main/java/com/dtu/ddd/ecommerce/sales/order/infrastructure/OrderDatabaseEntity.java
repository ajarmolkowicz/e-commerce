package com.dtu.ddd.ecommerce.sales.order.infrastructure;

import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.ShippingTime;
import com.dtu.ddd.ecommerce.sales.order.domain.SubmissionTime;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;


@NoArgsConstructor
class OrderDatabaseEntity {
  @Setter UUID id;
  @Setter Instant submission_time;
  @Setter Instant shipping_time;
  @Setter Integer version;

  Order toDomainModel(Collection<OrderItemDatabaseEntity> items) {
    return new Order(
        new OrderId(id),
        items.stream().map(OrderItemDatabaseEntity::toDomainModel).collect(Collectors.toSet()),
        new SubmissionTime(submission_time),
        Optional.ofNullable(shipping_time).map($ -> new ShippingTime(shipping_time)).orElse(null),
        new Version(version)
    );
  }
}
