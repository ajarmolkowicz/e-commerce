package com.dtu.ddd.ecommerce.sales;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableJdbcRepositories
@Import({SalesConfiguration.class})
public class SalesTestContext {
}
