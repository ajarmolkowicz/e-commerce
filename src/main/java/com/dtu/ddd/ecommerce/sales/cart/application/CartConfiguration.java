package com.dtu.ddd.ecommerce.sales.cart.application;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.cart.infrastructure.CartCrudRepository;
import com.dtu.ddd.ecommerce.sales.cart.infrastructure.CartDatabaseRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableJdbcRepositories
public class CartConfiguration {
  @Bean CartRepository cartRepository(JdbcTemplate jdbcTemplate, CartCrudRepository cartCrudRepository) {
    return new CartDatabaseRepository(jdbcTemplate, cartCrudRepository);
  }
}
