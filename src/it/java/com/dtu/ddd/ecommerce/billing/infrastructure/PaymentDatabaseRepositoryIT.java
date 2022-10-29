package com.dtu.ddd.ecommerce.billing.infrastructure;

import com.dtu.ddd.ecommerce.billing.BillingTestContext;
import com.dtu.ddd.ecommerce.billing.domain.*;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = BillingTestContext.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class PaymentDatabaseRepositoryIT {
  @Autowired
  private PaymentRepository paymentRepository;

  @Test
  void cru() {
    //WHEN
    assertThat(paymentRepository.find(PaymentId.generate())).isEmpty();

    //GIVEN
    final var referenceId = new ReferenceId(UUID.randomUUID());
    final var payment = new Payment(referenceId, Money.parse("EUR 100"));

    //WHEN
    paymentRepository.save(payment);

    //THEN
    final var saved = paymentRepository.find(payment.getId());
    assertThat(saved).isNotEmpty();
    assertThat(saved.get()).satisfies(
        $ -> assertThat($.getReferenceId()).isEqualTo(referenceId),
        $ -> assertThat($.getTotal()).isEqualTo(Money.parse("EUR 100")),
        $ -> assertThat($.getCollectionResult().collected()).isFalse(),
        $ -> assertThat($.getVersion()).isEqualTo(Version.zero())
    );

    //WHEN
    saved.get().onCollectionFinished(new CollectionResult(true));
    paymentRepository.save(saved.get());

    //THEN
    final var updated = paymentRepository.find(payment.getId());
    assertThat(updated).isNotEmpty();
    assertThat(updated.get()).satisfies(
        $ -> assertThat($.getCollectionResult().collected()).isTrue(),
        $ -> assertThat($.getVersion()).isEqualTo(Version.of(1))
    );
  }
}