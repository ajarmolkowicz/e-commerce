package com.dtu.ddd.ecommerce.sales.product;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.application.ProductApplicationService;
import com.dtu.ddd.ecommerce.sales.product.application.ProductEventHandler;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.infrastrucutre.ProductDatabaseRepository;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ProductConfiguration {

  // -- APPLICATION
  @Bean
  ProductApplicationService productApplicationService(ProductRepository productRepository,
                                                      OrderRepository orderRepository,
                                                      DomainEventPublisher eventPublisher) {
    return new ProductApplicationService(productRepository, orderRepository, eventPublisher);
  }

  @Bean
  ProductEventHandler productEventHandler(ProductRepository productRepository,
                                          OrderRepository orderRepository) {
    return new ProductEventHandler(productRepository, orderRepository);
  }


  // -- INFRASTRUCTURE
  @Bean
  ProductRepository productRepository(JdbcTemplate jdbcTemplate) {
    return new ProductDatabaseRepository(jdbcTemplate);
  }
}
