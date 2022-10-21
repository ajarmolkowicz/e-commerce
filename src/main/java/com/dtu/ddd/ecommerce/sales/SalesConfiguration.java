package com.dtu.ddd.ecommerce.sales;

import com.dtu.ddd.ecommerce.shared.db.DatabaseConfiguration;
import com.dtu.ddd.ecommerce.sales.cart.application.CartConfiguration;
import com.dtu.ddd.ecommerce.sales.order.application.OrderConfiguration;
import com.dtu.ddd.ecommerce.sales.product.application.ProductConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    DatabaseConfiguration.class,
    ProductConfiguration.class,
    OrderConfiguration.class,
    CartConfiguration.class,
    DomainEventConfiguration.class
})
public class SalesConfiguration {
}
