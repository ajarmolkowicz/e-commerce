package com.dtu.ddd.ecommerce.billing.domain;

import com.dtu.ddd.ecommerce.shared.event.DomainEvent;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

public interface PaymentEvents extends DomainEvent {
  @Value
  class PaymentCollected implements PaymentEvents {
    UUID eventId;
    Instant when;
    ReferenceId referenceId;

    public PaymentCollected(ReferenceId referenceId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.referenceId = referenceId;
    }
  }

  @Value
  class PaymentCollectionFailed implements PaymentEvents {
    UUID eventId;
    Instant when;
    ReferenceId referenceId;

    public PaymentCollectionFailed(ReferenceId referenceId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.referenceId = referenceId;
    }
  }
}
