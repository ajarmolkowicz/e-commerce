package com.dtu.ddd.ecommerce.sales.cart.application;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class CartEventListener {
  private final CartRepository cartRepository;

  @EventListener
  public void handle(OrderEvents.OrderSubmitted orderSubmitted) {
    final var cart = cartRepository.find(orderSubmitted.getCartId())
        .orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(orderSubmitted.getCartId()));

    cart.clear();

    cartRepository.save(cart);
  }
}
