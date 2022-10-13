package com.dtu.ddd.ecommerce.sales.product.application;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.infrastrucutre.ProductDatabaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ProductConfiguration {
  @Bean
  ProductRepository productRepository(JdbcTemplate jdbcTemplate) {
    return new ProductDatabaseRepository(jdbcTemplate);
  }
}
