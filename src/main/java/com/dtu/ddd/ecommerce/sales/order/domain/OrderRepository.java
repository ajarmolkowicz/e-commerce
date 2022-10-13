package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
  Optional<Order> find(OrderId id);
  void save(Order order);
  /*TODO : Move to read model */ List<Order> findNotDeliveredContainingProduct(ProductId productId);
}
