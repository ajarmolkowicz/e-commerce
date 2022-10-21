package com.dtu.ddd.ecommerce.sales.order.infrastructure;

import com.dtu.ddd.ecommerce.sales.SalesTestContext;
import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import java.util.Set;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SalesTestContext.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class OrderDatabaseRepositoryTest {
  @Autowired
  private OrderRepository orderRepository;

  @Test
  void cru() {
    //WHEN
    assertThat(orderRepository.find(OrderId.generate())).isEmpty();

    //GIVEN
    final var id1 = ProductId.generate();
    final var id2 = ProductId.generate();
    final var id3 = ProductId.generate();
    final var order = new Order(Set.of(
        new OrderItem(id1, Money.parse("EUR 25"), new Quantity(2)),
        new OrderItem(id2, Money.parse("EUR 100"), new Quantity(1)),
        new OrderItem(id3, Money.parse("EUR 30"), new Quantity(3))
    ));

    //WHEN
    orderRepository.save(order);

    //THEN
    final var saved = orderRepository.find(order.getId());
    assertThat(saved).isNotEmpty();
    assertThat(saved.get()).satisfies(
        $ -> assertThat($.getItems()).containsExactlyInAnyOrder(
            new OrderItem(id1, Money.parse("EUR 25"), new Quantity(2)),
            new OrderItem(id2, Money.parse("EUR 100"), new Quantity(1)),
            new OrderItem(id3, Money.parse("EUR 30"), new Quantity(3))
        ),
        $ -> assertThat($.total()).isEqualTo(Money.parse("EUR 240")),
        $ -> assertThat($.getSubmissionTime()).isNotNull(),
        $ -> assertThat($.getShippingTime()).isNull(),
        $ -> assertThat($.getVersion()).isEqualTo(Version.zero()));
  }
}