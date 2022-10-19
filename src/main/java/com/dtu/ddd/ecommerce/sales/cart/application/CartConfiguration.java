package com.dtu.ddd.ecommerce.sales.cart.application;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.cart.infrastructure.CartDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CartConfiguration {
  @Bean CartRepository cartRepository(JdbcTemplate jdbcTemplate) {
    return new CartDatabaseRepository(jdbcTemplate);
  }
}
