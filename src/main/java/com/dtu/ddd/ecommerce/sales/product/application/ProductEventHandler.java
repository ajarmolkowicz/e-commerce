package com.dtu.ddd.ecommerce.sales.product.application;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

@RequiredArgsConstructor
public class ProductEventHandler {
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;

  @EventListener @Order(3)
  public void handle(OrderEvents.OrderSubmitted orderSubmitted) {
    final var order = orderRepository.find(orderSubmitted.getOrderId())
        .orElseThrow(() -> new OrderRepository.Exceptions.OrderNotFound(orderSubmitted.getOrderId()));

    order.getItems().forEach(i -> {
      final var product = productRepository.find(i.productId()).orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(i.productId()));
      product.ordered(i.quantity());
      productRepository.save(product);
    });
  }
}
