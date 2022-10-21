package com.dtu.ddd.ecommerce.billing.domain;

public interface PaymentProvider {
    Boolean collect(ReferenceId id);
}
