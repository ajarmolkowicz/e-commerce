package com.dtu.ddd.ecommerce.sales.order.infrastructure;

import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.vavr.control.Try;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static java.lang.String.format;

@RequiredArgsConstructor
public class OrderDatabaseRepository implements OrderRepository {
  private static final Gson gson = new Gson();
  private final JdbcTemplate jdbcTemplate;

  @Override public void save(Order order) {
    find(order.getId())
        .map(entity -> update(order))
        .orElseGet(() -> insertNew(order));
  }

  @Override public List<Order> findNotDeliveredContainingProduct(ProductId productId) {
    /* TODO : Move to read model*/
    throw new UnsupportedOperationException();
  }

  @Override public Optional<Order> find(OrderId id) {
    final var o = Try.ofSupplier(
            () -> of(jdbcTemplate.queryForObject("SELECT o.* FROM orders o WHERE o.id = ?",
                new BeanPropertyRowMapper<>(OrderDatabaseEntity.class), id.id())))
        .getOrElse(none());
    return o.map($ -> Optional.of($.toDomainModel())).getOrElse(Optional.empty());
  }

  private int insertNew(Order order) {
    return jdbcTemplate.update(
        "INSERT INTO orders VALUES (?, ?, ?, ?, ?, ?)",
        order.getId().id(),
        gson.toJson(order.getItems(), new TypeToken<Set<OrderItem>>() {}.getType()),
        order.getTotal().toString(),
        order.getSubmissionTime().time().toString(),
        null,
        0);
  }

  private int update(Order order) {
    final var result = jdbcTemplate.update("UPDATE orders SET " +
            "total = ?, " +
            "items = ?, " +
            "submission_time = ?, " +
            "shipping_time = ?, " +
            "version = ?" +
            "WHERE id = ?" +
            "AND version = ?",
        order.getTotal().toString(),
        gson.toJson(order.getItems(), new TypeToken<Set<OrderItem>>() {}.getType()),
        order.getSubmissionTime().time().toString(),
        order.getShippingTime().time().toString(),
        order.getVersion().version() + 1,
        order.getId().id(),
        order.getVersion().version());
    if (result == 0) {
      throw new Exceptions.OrderIsStaleException(order.getId());
    }
    return result;
  }

  interface Exceptions {
    class OrderIsStaleException extends RuntimeException {
      OrderIsStaleException(OrderId id) {
        super(format("Order: %s aggregate root is stale", id.id()));
      }
    }
  }
}
