package com.dtu.ddd.ecommerce.sales.cart.application;

import com.dtu.ddd.ecommerce.sales.cart.application.command.AddProductToCartCommand;
import com.dtu.ddd.ecommerce.sales.cart.application.command.DeleteProductFromCartCommand;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartEvents;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CartApplicationService {
  private final CartRepository cartRepository;
  private final DomainEventPublisher eventPublisher;

  void addProductToCart(AddProductToCartCommand command) {
    final var cart = cartRepository.find(command.getCartId())
        .orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(command.getCartId()));

    cart.add(command.getProductId(), command.getQuantity());
    cartRepository.save(cart);

    eventPublisher.publish(new CartEvents.ProductAddedToCart(cart.getId(), command.getProductId(), command.getQuantity()));
  }

  void deleteProductFromCart(DeleteProductFromCartCommand command) {
    final var cart = cartRepository.find(command.getCartId())
        .orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(command.getCartId()));

    cart.delete(command.getProductId());
    cartRepository.save(cart);

    eventPublisher.publish(new CartEvents.ProductDeletedFromCart(cart.getId(), command.getProductId()));
  }
}
