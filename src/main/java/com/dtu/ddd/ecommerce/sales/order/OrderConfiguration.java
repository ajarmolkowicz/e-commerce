package com.dtu.ddd.ecommerce.sales.order;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.order.application.OrderApplicationService;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderService;
import com.dtu.ddd.ecommerce.sales.order.infrastructure.OrderDatabaseRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class OrderConfiguration {

  // -- APPLICATION
  @Bean
  OrderApplicationService orderApplicationService(OrderRepository orderRepository,
      CartRepository cartRepository,
      OrderService orderService,
      DomainEventPublisher eventPublisher) {
    return new OrderApplicationService(orderRepository, cartRepository, orderService, eventPublisher);
  }

  // -- DOMAIN
  @Bean
  OrderService orderService(ProductRepository productRepository) {
    return new OrderService(productRepository);
  }

  // -- INFRASTRUCTURE
  @Bean
  OrderRepository orderRepository(JdbcTemplate jdbcTemplate) {
    return new OrderDatabaseRepository(jdbcTemplate);
  }
}
