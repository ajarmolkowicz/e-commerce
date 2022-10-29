package com.dtu.ddd.ecommerce.billing.domain;

public record CollectionResult(Boolean result) {
  public Boolean collected() {
    return result != null && result == Boolean.TRUE;
  }
}
