package com.dtu.ddd.ecommerce.billing.infrastructure;

import com.dtu.ddd.ecommerce.billing.domain.*;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
class PaymentDatabaseEntity {
  @Setter UUID payment_id;
  @Setter UUID reference_id;
  @Setter Double total;
  @Setter String currency;
  @Setter Instant request_time;
  @Setter Boolean collection_result;
  @Setter Integer version;

  Payment toDomainModel() {
    return new Payment(
        PaymentId.of(payment_id),
        ReferenceId.of(reference_id),
        Money.of(CurrencyUnit.of(currency), total),
        new RequestTime(request_time),
        new CollectionResult(collection_result),
        new Version(version)
    );
  }
}
