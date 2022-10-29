package com.dtu.ddd.ecommerce.shipping;

import com.dtu.ddd.ecommerce.shared.db.DatabaseConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shipping.application.ShippingEventsHandler;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryRepository;
import com.dtu.ddd.ecommerce.shipping.infrastructure.DeliveryDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Import({
    DatabaseConfiguration.class,
    DomainEventConfiguration.class
})
public class ShippingConfiguration {

  // -- APPLICATION
  @Bean
  ShippingEventsHandler shippingEventsHandler(DeliveryRepository deliveryRepository, DomainEventPublisher eventPublisher) {
    return new ShippingEventsHandler(deliveryRepository, eventPublisher);
  }

  // -- INFRASTRUCTURE
  @Bean
  DeliveryRepository deliveryRepository(JdbcTemplate jdbcTemplate) {
    return new DeliveryDatabaseRepository(jdbcTemplate);
  }
}
