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

  @Override public Optional<Cart> find(CartId id) {
    final var o = Try.ofSupplier(
            () -> of(jdbcTemplate.queryForObject("SELECT c.* FROM CARTS c WHERE c.cart_id = ?",
                    new BeanPropertyRowMapper<>(CartDatabaseEntity.class), id.id())))
            .getOrElse(none());
    return o.map($ -> Optional.of($.toDomainModel(
            jdbcTemplate.query("SELECT i.* FROM cart_items i WHERE c.cart_id = ?",
                    new BeanPropertyRowMapper<>(CartItemDatabaseEntity.class), id.id())
    ))).getOrElse(Optional.empty());
  }

  @Override public void save(Cart cart) {
    find(cart.getId())
        .map(entity -> update(cart))
        .orElseGet(() -> insertNew(cart));
  }

  private int insertNew(Cart cart) {
    return jdbcTemplate.update("INSERT INTO CARTS VALUES (?, ?)", cart.getId().id(), 0);
  }

  private int update(Cart cart) {
    final var result = jdbcTemplate.update("UPDATE CARTS SET " +
            "version = ?" +
            "WHERE id = ?" +
            "AND version = ?",
        cart.getVersion().version() + 1,
        cart.getId().id(),
        cart.getVersion().version());
    if (result == 0) {
      throw new Exceptions.CartIsStaleException(cart.getId());
    }
    cart.getItems().forEach($ -> jdbcTemplate.update("UPDATE CART_ITEMS"));
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
