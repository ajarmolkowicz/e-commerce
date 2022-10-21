package com.dtu.ddd.ecommerce.billing;

import com.dtu.ddd.ecommerce.billing.application.PaymentEventsHandler;
import com.dtu.ddd.ecommerce.billing.domain.PaymentProvider;
import com.dtu.ddd.ecommerce.billing.domain.PaymentRepository;
import com.dtu.ddd.ecommerce.billing.infrastructure.PaymentDatabaseRepository;
import com.dtu.ddd.ecommerce.shared.db.DatabaseConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Import({
        DatabaseConfiguration.class,
        DomainEventConfiguration.class
})
public class BillingConfiguration {
    // -- REPOSITORIES
    @Bean
    PaymentRepository paymentRepository(JdbcTemplate jdbcTemplate) {
        return new PaymentDatabaseRepository(jdbcTemplate);
    }

    // -- EVENT HANDLERS
    @Bean
    PaymentEventsHandler paymentEventsHandler(PaymentRepository paymentRepository,
                                              DomainEventPublisher eventPublisher,
                                              PaymentProvider paymentProvider) {
        return new PaymentEventsHandler(paymentRepository, eventPublisher, paymentProvider);
    }

    // -- EXTERNAL
    @Bean
    PaymentProvider paymentProvider() {
        return id -> true;
    }
}
