package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import java.util.List;
import java.util.Optional;
import org.jmolecules.architecture.hexagonal.SecondaryPort;

import static java.lang.String.format;

@SecondaryPort
public interface OrderRepository {
  Optional<Order> find(OrderId id);

  void save(Order order);

  List<Order> findNotDeliveredContainingProduct(ProductId productId);

  interface Exceptions {
    class OrderNotFound extends BusinessException {
      public OrderNotFound(OrderId id) {
        super(format("Order with id: %s not found", id.id().toString()));
      }
    }
  }
}
