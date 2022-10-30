package com.dtu.ddd.ecommerce.sales.order.application;

import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shipping.domain.OrderId;
import com.dtu.ddd.ecommerce.shipping.domain.ShippingEvents;
import java.util.Optional;
import java.util.Set;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.dtu.ddd.ecommerce.utils.Assertions.assertCaptureSatisfies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEventHandlerTest {
  @Mock private OrderRepository orderRepository;

  private OrderEventHandler handler;

  @BeforeEach
  void setUp() {
    handler = new OrderEventHandler(orderRepository);
  }

  @DisplayName("Order shipped, should order shipping time be set")
  @Test
  void order_shipped() {
    //GIVEN
    final var order = new Order(Set.of(new OrderItem(ProductId.generate(), Money.parse("EUR 20"), new Quantity(2))));
    when(orderRepository.find(order.getId())).thenReturn(Optional.of(order));

    //WHEN
    handler.handle(new ShippingEvents.OrderShipped(OrderId.of(order.getId().id())));

    //THEN
    assertCaptureSatisfies($ -> verify(orderRepository).save($.capture()),
        captured -> assertThat(captured.getShippingTime()).isNotNull(), Order.class);
  }
}