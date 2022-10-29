package com.dtu.ddd.ecommerce.sales.cart.domain;

import java.util.Optional;

import static java.lang.String.format;

public interface CartRepository {
  Optional<Cart> find(CartId id);

  void save(Cart cart);

  interface Exceptions {
    class CartNotFound extends RuntimeException {
      public CartNotFound(CartId id) {
        super(format("Cart with id: %s not found", id.id().toString()));
      }
    }
  }
}
