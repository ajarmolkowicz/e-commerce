package com.dtu.ddd.ecommerce.sales;

import com.dtu.ddd.ecommerce.shared.db.DatabaseConfiguration;
import com.dtu.ddd.ecommerce.sales.cart.CartConfiguration;
import com.dtu.ddd.ecommerce.sales.order.OrderConfiguration;
import com.dtu.ddd.ecommerce.sales.product.ProductConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    DatabaseConfiguration.class,
    DomainEventConfiguration.class,
    ProductConfiguration.class,
    OrderConfiguration.class,
    CartConfiguration.class
})
public class SalesConfiguration {
}
