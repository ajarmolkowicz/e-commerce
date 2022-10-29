package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

public interface OrderRepository {
  Optional<Order> find(OrderId id);

  void save(Order order);

  /*TODO : Move to read model */ List<Order> findNotDeliveredContainingProduct(ProductId productId);

  interface Exceptions {
    class OrderNotFound extends RuntimeException {
      public OrderNotFound(OrderId id) {
        super(format("Order with id: %s not found", id.id().toString()));
      }
    }
  }
}
