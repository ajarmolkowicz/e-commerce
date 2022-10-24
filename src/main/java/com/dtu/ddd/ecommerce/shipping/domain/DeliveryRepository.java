package com.dtu.ddd.ecommerce.shipping.domain;

import java.util.Optional;

import static java.lang.String.format;

public interface DeliveryRepository {
    Optional<Delivery> find(DeliveryId id);
    Optional<Delivery> findByOrderId(OrderId id);
    void save(Delivery delivery);
    void delete(DeliveryId id);

    interface Exceptions {
        class DeliveryNotFound extends RuntimeException {
            public DeliveryNotFound(OrderId orderId) {
                super(format("Delivery with delivery_id: %s not found", orderId.id().toString()));
            }
        }
    }
}
