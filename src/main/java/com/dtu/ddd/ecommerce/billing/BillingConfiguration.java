package com.dtu.ddd.ecommerce.billing;

import com.dtu.ddd.ecommerce.billing.application.PaymentEventsHandler;
import com.dtu.ddd.ecommerce.billing.domain.PaymentProvider;
import com.dtu.ddd.ecommerce.billing.domain.PaymentRepository;
import com.dtu.ddd.ecommerce.billing.domain.ReferenceId;
import com.dtu.ddd.ecommerce.billing.infrastructure.PaymentDatabaseRepository;
import com.dtu.ddd.ecommerce.shared.db.DatabaseConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventConfiguration;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import lombok.SneakyThrows;
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

  // -- APPLICATION
  @Bean
  PaymentEventsHandler paymentEventsHandler(PaymentRepository paymentRepository,
                                            DomainEventPublisher eventPublisher,
                                            PaymentProvider paymentProvider) {
    return new PaymentEventsHandler(paymentRepository, eventPublisher, paymentProvider);
  }

  // -- REPOSITORIES
  @Bean
  PaymentRepository paymentRepository(JdbcTemplate jdbcTemplate) {
    return new PaymentDatabaseRepository(jdbcTemplate);
  }

  // -- EXTERNAL
  @Bean
  PaymentProvider paymentProvider() {
    return new DelayedPaymentProvider();
  }

  private static class DelayedPaymentProvider implements PaymentProvider {
    @Override @SneakyThrows
    public Boolean collect(ReferenceId id) {
      Thread.sleep(1000);
      return true;
    }
  }
}
