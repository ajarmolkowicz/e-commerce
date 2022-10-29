package com.dtu.ddd.ecommerce.sales.order.application;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.order.application.commands.SubmitOrderCommand;
import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderService;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@RequiredArgsConstructor
public class OrderApplicationService {
  private final OrderRepository orderRepository;
  private final CartRepository cartRepository;
  private final OrderService orderService;
  private final DomainEventPublisher eventPublisher;

  public void submitOrder(SubmitOrderCommand command) {
    final var cart = cartRepository.find(command.getCartId()).orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(command.getCartId()));
    if (!orderService.enoughProductsForAnOrder(cart)) {
      eventPublisher.publish(new OrderEvents.OrderSubmissionFailed(command.getCartId()));
      throw new Exceptions.OrderDemandNotSatisfied(command.getCartId());
    } else {
      final var order = new Order(orderService.assignPricesToItems(cart.getItems()));
      orderRepository.save(order);
      eventPublisher.publish(new OrderEvents.OrderSubmitted(order.getId(), command.getCartId(), order.total(), command.getAddress()));
    }
  }

  public interface Exceptions {
    class OrderDemandNotSatisfied extends BusinessException {
      public OrderDemandNotSatisfied(CartId id) {
        super(format("Product demand of cart with id: %s is not satisfied", id.id().toString()));
      }
    }
  }
}
