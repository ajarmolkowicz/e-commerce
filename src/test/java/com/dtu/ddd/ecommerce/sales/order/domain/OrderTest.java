package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.util.Sets;
import org.joda.money.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
  @DisplayName("Order contains of a few items with different quantity, should total value match")
  @Test
  void orderWithAFewOrderItems() {
    //GIVEN
    final var items = Set.of(
        new OrderItem(ProductId.generate(), Money.parse("EUR 100"), new Quantity(2)),
        new OrderItem(ProductId.generate(), Money.parse("EUR 50"), new Quantity(1)),
        new OrderItem(ProductId.generate(), Money.parse("EUR 10"), new Quantity(3))
    );

    //WHEN
    final var order = new Order(items);

    //THEN
    assertThat(order.total()).isEqualTo(Money.parse("EUR 280"));
  }
}