package com.dtu.ddd.ecommerce.sales.order.application;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.ShippingTime;
import com.dtu.ddd.ecommerce.shipping.domain.ShippingEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class OrderEventHandler {
  private final OrderRepository orderRepository;

  @EventListener
  public void handle(ShippingEvents.OrderShipped orderShipped) {
    final var id = OrderId.of(orderShipped.getOrderId().id());
    final var order = orderRepository.find(id).orElseThrow(() -> new OrderRepository.Exceptions.OrderNotFound(id));

    order.shipped(new ShippingTime(orderShipped.getWhen()));

    orderRepository.save(order);
  }
}
