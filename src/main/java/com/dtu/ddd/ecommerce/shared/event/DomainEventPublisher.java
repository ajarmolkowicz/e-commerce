package com.dtu.ddd.ecommerce.shared.event;

import java.util.Collection;

public interface DomainEventPublisher {
  void publish(DomainEvent event);

  void publish(Collection<DomainEvent> event);
}
