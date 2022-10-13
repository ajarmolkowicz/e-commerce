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
    final var cartId = new CartId(command.cartId());
    final var cart = cartRepository.find(cartId)
        .orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(cartId));

    final var productId = new ProductId(command.productId());
    final var quantity = new Quantity(command.quantity());
    cart.add(productId, quantity);
    cartRepository.save(cart);

    eventPublisher.publish(new CartEvents.ProductAddedToCart(cart.getId(), productId, quantity));
  }

  void deleteProductFromCart(DeleteProductFromCartCommand command) {
    final var cartId = new CartId(command.cartId());
    final var cart = cartRepository.find(cartId)
        .orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(cartId));

    final var productId = new ProductId(command.productId());
    cart.delete(productId);
    cartRepository.save(cart);

    eventPublisher.publish(new CartEvents.ProductDeletedFromCart(cart.getId(), productId));
  }
}
