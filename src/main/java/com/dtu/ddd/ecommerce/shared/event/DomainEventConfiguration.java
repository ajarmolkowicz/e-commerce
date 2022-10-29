package com.dtu.ddd.ecommerce.shared.event;

import java.util.Collection;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventConfiguration {
  @Bean
  DomainEventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    return new DomainEventPublisher() {
      @Override
      public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
      }

      @Override
      public void publish(Collection<DomainEvent> events) {
        events.forEach(this::publish);
      }
    };
  }
}
