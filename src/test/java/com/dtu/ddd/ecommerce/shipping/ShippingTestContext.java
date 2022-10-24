package com.dtu.ddd.ecommerce.shipping;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ShippingConfiguration.class})
public class ShippingTestContext {
}
