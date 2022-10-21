package com.dtu.ddd.ecommerce.billing.domain;

import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;
import org.joda.money.Money;

import java.time.Instant;

@AggregateRoot @Entity
public class Payment {
  @Getter private PaymentId id;
  @Getter private ReferenceId referenceId;
  @Getter private Money total;
  @Getter private RequestTime requestTime;
  @Getter private CollectionResult collectionResult;
  @Getter private Version version;

  public Payment(ReferenceId referenceId, Money total) {
    this.id = PaymentId.generate();
    this.referenceId = referenceId;
    this.total = total;
    this.requestTime = new RequestTime(Instant.now());
  }

  public Payment(PaymentId id, ReferenceId referenceId, Money total, RequestTime requestTime, CollectionResult collectionResult, Version version) {
    this.id = id;
    this.referenceId = referenceId;
    this.total = total;
    this.requestTime = requestTime;
    this.collectionResult = collectionResult;
    this.version = version;
  }

  public void onCollectionFinished(CollectionResult result) {
    this.collectionResult = result;
  }
}
