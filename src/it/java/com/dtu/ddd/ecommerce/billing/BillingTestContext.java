package com.dtu.ddd.ecommerce.billing;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({BillingConfiguration.class})
public class BillingTestContext {
}
