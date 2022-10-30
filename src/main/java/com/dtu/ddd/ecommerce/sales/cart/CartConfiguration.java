package com.dtu.ddd.ecommerce.sales.cart;

import com.dtu.ddd.ecommerce.sales.cart.application.CartApplicationService;
import com.dtu.ddd.ecommerce.sales.cart.application.CartEventListener;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.cart.infrastructure.CartDatabaseRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CartConfiguration {

  // -- APPLICATION
  @Bean
  CartApplicationService cartApplicationService(CartRepository cartRepository, ProductRepository productRepository, DomainEventPublisher domainEventPublisher) {
    return new CartApplicationService(cartRepository, productRepository, domainEventPublisher);
  }

  @Bean
  CartEventListener cartEventListener(CartRepository cartRepository) {
    return new CartEventListener(cartRepository);
  }

  // -- INFRASTRUCTURE
  @Bean
  CartRepository cartRepository(JdbcTemplate jdbcTemplate) {
    return new CartDatabaseRepository(jdbcTemplate);
  }
}
