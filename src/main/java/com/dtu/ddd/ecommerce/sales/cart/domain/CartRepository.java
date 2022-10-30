package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import java.util.Optional;
import org.jmolecules.architecture.hexagonal.SecondaryPort;

import static java.lang.String.format;

@SecondaryPort
public interface CartRepository {
  Optional<Cart> find(CartId id);

  void save(Cart cart);

  interface Exceptions {
    class CartNotFound extends BusinessException {
      public CartNotFound(CartId id) {
        super(format("Cart with id: %s not found", id.id().toString()));
      }
    }
  }
}
