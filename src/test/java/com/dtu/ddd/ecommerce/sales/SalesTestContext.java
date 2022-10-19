package com.dtu.ddd.ecommerce.sales;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SalesConfiguration.class})
public class SalesTestContext {
}
