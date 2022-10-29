package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;

import static java.lang.String.format;

@AggregateRoot
@Entity
public class Cart {
  @Getter private final CartId id;
  @Getter private final Set<CartItem> items;
  @Getter private Version version;

  public Cart() {
    this.id = new CartId(UUID.randomUUID());
    this.items = new HashSet<>();
  }

  public Cart(CartId cartId) {
    this.id = cartId;
    this.items = new HashSet<>();
  }

  public Cart(CartId id, Set<CartItem> items, Version version) {
    this.id = id;
    this.items = items;
    this.version = version;
  }

  public void add(ProductId productId, Quantity quantity) {
    this.items.stream().filter($ -> $.getProductId().equals(productId)).findAny().ifPresent($ -> {
      throw new Exceptions.CartAlreadyContainsProductException(this.id, productId);
    });
    this.items.add(new CartItem(productId, quantity));
  }

  public void delete(ProductId productId) {
    final var cartItem = this.items.stream()
        .filter($ -> $.getProductId().equals(productId))
        .findAny()
        .orElseThrow(() -> new Exceptions.CartDoesNotContainProductException(this.id, productId));
    this.items.remove(cartItem);
  }

  public interface Exceptions {
    class CartAlreadyContainsProductException extends RuntimeException {
      public CartAlreadyContainsProductException(CartId cartId, ProductId productId) {
        super(format("Cart with id: %s already contains product with id: %s", cartId.id(), productId.id()));
      }
    }

    class CartDoesNotContainProductException extends RuntimeException {
      public CartDoesNotContainProductException(CartId cartId, ProductId productId) {
        super(format("Cart with id: %s does not contain product with id: %s", cartId.id(), productId.id()));
      }
    }
  }
}
