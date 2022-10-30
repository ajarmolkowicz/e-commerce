package com.dtu.ddd.ecommerce.billing.domain;

import java.util.Optional;
import org.jmolecules.architecture.hexagonal.SecondaryPort;

@SecondaryPort
public interface PaymentRepository {
  Optional<Payment> find(PaymentId id);

  void save(Payment payment);
}
