package com.dtu.ddd.ecommerce.sales.cart;

import com.dtu.ddd.ecommerce.sales.cart.application.CartApplicationService;
import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.cart.infrastructure.CartDatabaseRepository;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CartConfiguration {

  // -- APPLICATION
  @Bean
  CartApplicationService cartApplicationService(CartRepository cartRepository, DomainEventPublisher domainEventPublisher) {
    return new CartApplicationService(cartRepository, domainEventPublisher);
  }

  // -- INFRASTRUCTURE
  @Bean
  CartRepository cartRepository(JdbcTemplate jdbcTemplate) {
    return new CartDatabaseRepository(jdbcTemplate);
  }
}
