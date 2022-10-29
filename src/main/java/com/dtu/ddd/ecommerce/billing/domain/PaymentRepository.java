package com.dtu.ddd.ecommerce.billing.domain;

import java.util.Optional;

public interface PaymentRepository {
  Optional<Payment> find(PaymentId id);

  void save(Payment payment);
}
