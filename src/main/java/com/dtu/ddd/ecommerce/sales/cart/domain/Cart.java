package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import static java.lang.String.format;

@AggregateRoot
@Entity
public class Cart {
  @Identity @Getter private final CartId id;
  private final Set<CartItem> items;
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

  public Set<CartItem> getItems() {
    return Collections.unmodifiableSet(items);
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

  public void clear() {
    items.clear();
  }

  public interface Exceptions {
    class CartAlreadyContainsProductException extends BusinessException {
      public CartAlreadyContainsProductException(CartId cartId, ProductId productId) {
        super(format("Cart with id: %s already contains product with id: %s", cartId.id(), productId.id()));
      }
    }

    class CartDoesNotContainProductException extends BusinessException {
      public CartDoesNotContainProductException(CartId cartId, ProductId productId) {
        super(format("Cart with id: %s does not contain product with id: %s", cartId.id(), productId.id()));
      }
    }
  }
}
