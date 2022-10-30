package com.dtu.ddd.ecommerce.shipping.domain;

import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import java.util.Optional;
import org.jmolecules.architecture.hexagonal.SecondaryPort;

import static java.lang.String.format;

@SecondaryPort
public interface DeliveryRepository {
  Optional<Delivery> find(DeliveryId id);

  Optional<Delivery> findByOrderId(OrderId id);

  void save(Delivery delivery);

  void delete(DeliveryId id);

  interface Exceptions {
    class DeliveryNotFound extends BusinessException {
      public DeliveryNotFound(OrderId orderId) {
        super(format("Delivery with order_id: %s not found", orderId.id().toString()));
      }
    }
  }
}
