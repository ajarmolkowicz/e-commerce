package com.dtu.ddd.ecommerce.shipping.domain;

import java.util.Optional;

public interface DeliveryRepository {
    Optional<Delivery> find(DeliveryId id);
    void save(Delivery delivery);
}
