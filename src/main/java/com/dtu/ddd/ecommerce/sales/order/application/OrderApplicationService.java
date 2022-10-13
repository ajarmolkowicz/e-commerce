package com.dtu.ddd.ecommerce.sales.order.application;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.order.application.commands.SubmitOrderCommand;
import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderService;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class OrderApplicationService {
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final OrderService orderService;
  private final DomainEventPublisher eventPublisher;

  void submitOrder(SubmitOrderCommand command) {
    final var cartId = new CartId(command.cartId());
    final var cart = cartRepository.find(cartId).orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(cartId));
    if (!orderService.enoughProductsForAnOrder(cart)) {
      eventPublisher.publish(new OrderEvents.OrderSubmissionFailed(cartId));
    } else {
      final var order = new Order(orderService.assignPricesToItems(cart.getItems()));
      orderRepository.save(order);
      eventPublisher.publish(new OrderEvents.OrderSubmitted(order.getId(), cartId));
    }
  }
}
