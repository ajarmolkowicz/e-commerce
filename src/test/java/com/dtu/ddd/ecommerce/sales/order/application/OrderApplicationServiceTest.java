package com.dtu.ddd.ecommerce.sales.order.application;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.order.application.commands.SubmitOrderCommand;
import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderService;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import java.util.Optional;
import java.util.Set;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.dtu.ddd.ecommerce.utils.Assertions.assertCaptureSatisfies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {
  @Mock private OrderRepository orderRepository;
  @Mock private CartRepository cartRepository;
  @Mock private OrderService orderService;
  @Mock private DomainEventPublisher eventPublisher;

  private OrderApplicationService service;

  @BeforeEach
  void setUp() {
    this.service =
        new OrderApplicationService(orderRepository, cartRepository, orderService, eventPublisher);
  }

  @DisplayName("Not enough products in the warehouse, should order not be submitted and event should be published")
  @Test
  void notEnoughProducts() {
    //GIVEN
    final var cart = new Cart();
    when(orderService.enoughProductsForAnOrder(cart)).thenReturn(false);
    when(cartRepository.find(cart.getId())).thenReturn(Optional.of(cart));

    //WHEN
    service.submitOrder(new SubmitOrderCommand(cart.getId().id()));

    //THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getCartId()).isEqualTo(cart.getId()),
        OrderEvents.OrderSubmissionFailed.class);
  }

  @DisplayName("Enough products in the warehouse, should order be submitted and appropriate event be published")
  @Test
  void enoughProducts() {
    //GIVEN
    final var cart = new Cart();
    final var productId1 = ProductId.generate();
    final var productId2 = ProductId.generate();
    final var productId3 = ProductId.generate();
    cart.add(productId1, new Quantity(2));
    cart.add(productId2, new Quantity(1));
    cart.add(productId3, new Quantity(3));
    final var orderItems = Set.of(
        new OrderItem(productId1, Money.parse("EUR 100"), new Quantity(2)),
        new OrderItem(productId2, Money.parse("EUR 50"), new Quantity(1)),
        new OrderItem(productId3, Money.parse("EUR 10"), new Quantity(3))
    );
    when(orderService.enoughProductsForAnOrder(cart)).thenReturn(true);
    when(cartRepository.find(cart.getId())).thenReturn(Optional.of(cart));
    when(orderService.assignPricesToItems(anySet())).thenReturn(orderItems);

    //WHEN
    service.submitOrder(new SubmitOrderCommand(cart.getId().id()));

    //THEN
    final var captor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository).save(captor.capture());
    final var captured = captor.getValue();
    assertThat(captured.getTotal()).isEqualTo(Money.parse("EUR 280"));

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> {
          assertThat(event.getCartId()).isEqualTo(cart.getId());
          assertThat(event.getOrderId()).isEqualTo(captured.getId());
        },
        OrderEvents.OrderSubmitted.class);
  }
}