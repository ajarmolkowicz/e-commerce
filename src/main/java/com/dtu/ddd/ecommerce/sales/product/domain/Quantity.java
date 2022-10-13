package com.dtu.ddd.ecommerce.sales.product.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public final class Quantity {
  public static final Quantity ZERO = new Quantity(0);
  public static final Quantity ONE = new Quantity(1);
  private final Integer quantity;

  public Quantity(int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Quantity cannot be less than zero");
    }
    this.quantity = quantity;
  }

  public Integer value() {
    return quantity;
  }

  public Quantity add(Quantity addend) {
    return new Quantity(quantity + addend.value());
  }

  public Quantity add(int addend) {
    return new Quantity(quantity + addend);
  }

  public Boolean isPositiveOrZero() {
    return quantity >= 0;
  }

  public Boolean isGreaterOrEqual(Quantity other) {
    return this.quantity >= other.value();
  }
}
