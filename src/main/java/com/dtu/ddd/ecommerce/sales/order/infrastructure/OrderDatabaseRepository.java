package com.dtu.ddd.ecommerce.sales.order.infrastructure;

import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import io.vavr.control.Try;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static java.lang.String.format;

@RequiredArgsConstructor
public class OrderDatabaseRepository implements OrderRepository {
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<Order> find(OrderId id) {
    final var o = Try.ofSupplier(
            () -> of(jdbcTemplate.queryForObject("SELECT o.* FROM orders o WHERE o.order_id = ?",
                new BeanPropertyRowMapper<>(OrderDatabaseEntity.class), id.id())))
        .getOrElse(none());
    return o.map($ -> Optional.of($.toDomainModel(findItems(id)))).getOrElse(Optional.empty());
  }

  @Override
  public List<Order> findNotDeliveredContainingProduct(ProductId productId) {
    return jdbcTemplate.query("SELECT o.* FROM orders o JOIN order_items i ON o.order_id = i.order_id WHERE o.shipping_time IS NULL AND i.product_id = ?",
        new BeanPropertyRowMapper<>(OrderDatabaseEntity.class), productId.id())
        .stream().map($ -> $.toDomainModel(findItems(new OrderId($.order_id))))
        .collect(Collectors.toList());
  }

  @Override
  public void save(Order order) {
    find(order.getId())
        .ifPresentOrElse(
            entity -> update(order),
            () -> {
              insertNew(order);
              order.getItems().forEach($ -> save(order.getId(), $));
            }
        );
  }

  private void insertNew(Order order) {
    jdbcTemplate.update(
        "INSERT INTO orders VALUES (?, ?, ?, ?)",
        order.getId().id(),
        order.getSubmissionTime().time().toString(),
        null,
        0);
  }

  private void update(Order order) {
    final var result = jdbcTemplate.update("UPDATE orders SET " +
            "submission_time = ?, " +
            "shipping_time = ?, " +
            "version = ?" +
            "WHERE order_id = ?" +
            "AND version = ?",
        order.getSubmissionTime().time(),
        order.getShippingTime().time(),
        order.getVersion().version() + 1,
        order.getId().id(),
        order.getVersion().version());

    final var productIds = findItems(order.getId()).stream().map(item -> item.product_id).collect(Collectors.toSet());
    order.getItems()
        .forEach(item -> save(order.getId(), item));
    productIds.stream()
        .filter(productId -> order.getItems().stream().noneMatch(item -> item.productId().id().equals(productId)))
        .forEach(productId -> delete(order.getId(), new ProductId(productId)));

    if (result == 0) {
      throw new Exceptions.OrderIsStaleException(order.getId());
    }
  }

  interface Exceptions {
    class OrderIsStaleException extends RuntimeException {
      OrderIsStaleException(OrderId id) {
        super(format("Order: %s aggregate root is stale", id.id()));
      }
    }

    class OrderItemUpdateException extends RuntimeException {
      OrderItemUpdateException(OrderId orderId, ProductId productId) {
        super(format("Order: %s; Order item (product: %s), could not be updated", orderId.id(), productId.id()));
      }
    }

    class OrderItemDeleteException extends RuntimeException {
      OrderItemDeleteException(OrderId orderId, ProductId productId) {
        super(format("Order: %s; Order item (product: %s), could not be deleted", orderId.id(), productId.id()));
      }
    }
  }

  private Optional<OrderItemDatabaseEntity> find(OrderId orderId, ProductId productId) {
    final var o = Try.ofSupplier(() -> of(
        jdbcTemplate.queryForObject("SELECT i.* FROM order_items i " +
                "WHERE i.order_id = ? " +
                "AND i.product_id = ?",
            new BeanPropertyRowMapper<>(OrderItemDatabaseEntity.class),
            orderId.id(), productId.id()))).getOrElse(none());
    return o.map(Optional::of).getOrElse(Optional.empty());
  }

  private Collection<OrderItemDatabaseEntity> findItems(OrderId id) {
    return jdbcTemplate.query("SELECT i.* FROM order_items i WHERE i.order_id = ?",
        new BeanPropertyRowMapper<>(OrderItemDatabaseEntity.class), id.id());
  }

  private void save(OrderId id, OrderItem item) {
    find(id, item.productId())
        .map(entity -> update(id, item))
        .orElseGet(() -> insertNew(id, item));
  }

  private int insertNew(OrderId id, OrderItem item) {
    return jdbcTemplate.update(
        "INSERT INTO order_items VALUES (?, ?, ?, ?, ?)",
        id.id(),
        item.productId().id(),
        item.money().getAmount().doubleValue(),
        item.money().getCurrencyUnit().toString(),
        item.quantity().value()
    );
  }

  private int update(OrderId orderId, OrderItem item) {
    final var result = jdbcTemplate.update("UPDATE order_items SET " +
            "money = ?, " +
            "currency = ?, " +
            "quantity = ?" +
            "WHERE order_id = ?" +
            "AND product_id = ?",
        item.money().getAmount().doubleValue(),
        item.money().getCurrencyUnit().toString(),
        item.quantity().value(),
        orderId.id(),
        item.productId().id());
    if (result == 0) {
      throw new Exceptions.OrderItemUpdateException(orderId, item.productId());
    }
    return result;
  }

  private void delete(OrderId orderId, ProductId productId) {
    final var result = jdbcTemplate.update("DELETE FROM order_items " +
            "WHERE order_id = ?" +
            "AND product_id = ?",
        orderId.id(),
        productId);
    if (result == 0) {
      throw new Exceptions.OrderItemDeleteException(orderId, productId);
    }
  }
}
