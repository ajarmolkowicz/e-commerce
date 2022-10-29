package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.cart.domain.*;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static java.lang.String.format;

@RequiredArgsConstructor
public class CartDatabaseRepository implements CartRepository {
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Cart> find(CartId id) {
    final var o = Try.ofSupplier(() -> of(
        jdbcTemplate.queryForObject("SELECT c.* FROM carts c WHERE c.cart_id = ?",
            new BeanPropertyRowMapper<>(CartDatabaseEntity.class), id.id()))).getOrElse(none());
    return o.map($ -> Optional.of($.toDomainModel(findItems(id)))).getOrElse(Optional.empty());
  }

  @Override
  public void save(Cart cart) {
    find(cart.getId())
        .ifPresentOrElse(
            entity -> update(cart),
            () -> {
              insertNew(cart);
              cart.getItems().forEach($ -> save(cart.getId(), $));
            }
        );
  }

  private void insertNew(Cart cart) {
    jdbcTemplate.update("INSERT INTO carts VALUES (?, ?)", cart.getId().id(), 0);
  }

  private void update(Cart cart) {
    final var result = jdbcTemplate.update("UPDATE carts SET " +
            "version = ?" +
            "WHERE cart_id = ?" +
            "AND version = ?",
        cart.getVersion().version() + 1,
        cart.getId().id(),
        cart.getVersion().version());

    final var itemIds = findItems(cart.getId()).stream()
        .map(item -> item.cart_item_id).collect(Collectors.toSet());
    cart.getItems()
        .forEach(item -> save(cart.getId(), item));
    itemIds.stream()
        .filter(itemId -> cart.getItems().stream().noneMatch(item -> item.getId().id().equals(itemId)))
        .forEach(itemId -> delete(cart.getId(), new CartItemId(itemId)));

    if (result == 0) {
      throw new Exceptions.CartIsStaleException(cart.getId());
    }
  }

  interface Exceptions {
    class CartIsStaleException extends RuntimeException {
      CartIsStaleException(CartId id) {
        super(format("Cart: %s aggregate root is stale", id.id()));
      }
    }

    class CartItemUpdateException extends RuntimeException {
      CartItemUpdateException(CartId cartId, CartItemId itemId) {
        super(format("Cart: %s; Cart item: %s could not be updated", cartId.id(), itemId.id()));
      }
    }

    class CartItemDeleteException extends RuntimeException {
      CartItemDeleteException(CartId cartId, CartItemId itemId) {
        super(format("Cart: %s; Cart item: %s could not be deleted", cartId.id(), itemId.id()));
      }
    }
  }

  private Optional<CartItemDatabaseEntity> find(CartItemId id) {
    final var o = Try.ofSupplier(() -> of(
        jdbcTemplate.queryForObject("SELECT i.* FROM cart_items i WHERE i.cart_item_id = ?",
            new BeanPropertyRowMapper<>(CartItemDatabaseEntity.class), id.id()))).getOrElse(none());
    return o.map(Optional::of).getOrElse(Optional.empty());
  }

  private Collection<CartItemDatabaseEntity> findItems(CartId id) {
    return jdbcTemplate.query("SELECT i.* FROM cart_items i WHERE i.cart_id = ?",
        new BeanPropertyRowMapper<>(CartItemDatabaseEntity.class), id.id());
  }

  private void save(CartId id, CartItem item) {
    find(item.getId())
        .map(entity -> update(id, item))
        .orElseGet(() -> insertNew(id, item));
  }

  private int insertNew(CartId cartId, CartItem item) {
    return jdbcTemplate.update(
        "INSERT INTO cart_items VALUES (?, ?, ?, ?)",
        item.getId().id(),
        cartId.id(),
        item.getProductId().id(),
        item.getQuantity().value());
  }

  private int update(CartId cartId, CartItem item) {
    final var result = jdbcTemplate.update("UPDATE cart_items SET " +
            "quantity = ?" +
            "WHERE cart_id = ?" +
            "AND cart_item_id = ?",
        item.getQuantity().value(),
        cartId.id(),
        item.getId().id());
    if (result == 0) {
      throw new Exceptions.CartItemUpdateException(cartId, item.getId());
    }
    return result;
  }

  private void delete(CartId cartId, CartItemId itemId) {
    final var result = jdbcTemplate.update("DELETE FROM cart_items " +
            "WHERE cart_id = ?" +
            "AND cart_item_id = ?",
        cartId.id(),
        itemId.id());
    if (result == 0) {
      throw new Exceptions.CartItemDeleteException(cartId, itemId);
    }
  }
}
