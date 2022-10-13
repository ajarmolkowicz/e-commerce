package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItem;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vavr.control.Try;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static java.lang.String.format;

@RequiredArgsConstructor
public class CartDatabaseRepository implements CartRepository {
  private static final Gson gson = new Gson();
  private final JdbcTemplate jdbcTemplate;
  private final CartCrudRepository cartCrudRepository;

  @Override public Optional<Cart> find(CartId id) {
    return cartCrudRepository.findById(id.id()).map($ -> $.toDomainModel());
    //final var o = Try.ofSupplier(
    //        () -> of(jdbcTemplate.queryForObject("SELECT c.* FROM carts c WHERE c.id = ?",
    //            new BeanPropertyRowMapper<>(CartDatabaseEntity.class), id.id())))
    //    .getOrElse(none());
    //return o.map($ -> Optional.of($.toDomainModel())).getOrElse(Optional.empty());
  }

  @Override public void save(Cart cart) {
    cartCrudRepository.save(CartDatabaseEntity.fromDomainModel(cart));
    //find(cart.getId())
    //    .map(entity -> update(cart))
    //    .orElseGet(() -> insertNew(cart));
  }

  private int insertNew(Cart cart) {
    return jdbcTemplate.update(
        "INSERT INTO carts VALUES (?, ?, ?)",
        cart.getId().id(),
        gson.toJson(cart.getItems(), new TypeToken<Set<CartItem>>() {
        }.getType()),
        0);
  }

  private int update(Cart cart) {
    final var result = jdbcTemplate.update("UPDATE carts SET " +
            "items = ?, " +
            "version = ?" +
            "WHERE id = ?" +
            "AND version = ?",
        gson.toJson(cart.getItems(), new TypeToken<Set<CartItem>>() {
        }.getType()),
        cart.getVersion().version() + 1,
        cart.getId().id(),
        cart.getVersion().version());
    if (result == 0) {
      throw new Exceptions.CartIsStaleException(cart.getId());
    }
    return result;
  }

  interface Exceptions {
    class CartIsStaleException extends RuntimeException {
      CartIsStaleException(CartId id) {
        super(format("Cart: %s aggregate root is stale", id.id()));
      }
    }
  }
}
