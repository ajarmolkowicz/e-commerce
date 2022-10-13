package com.dtu.ddd.ecommerce.shared.event;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {
  UUID getEventId();
  Instant getWhen();
}
