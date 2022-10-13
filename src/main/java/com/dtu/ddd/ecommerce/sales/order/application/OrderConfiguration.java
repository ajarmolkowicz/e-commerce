package com.dtu.ddd.ecommerce.sales.order.application;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.order.infrastructure.OrderDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class OrderConfiguration {
  @Bean
  OrderRepository orderRepository(JdbcTemplate jdbcTemplate) {
    return new OrderDatabaseRepository(jdbcTemplate);
  }
}
