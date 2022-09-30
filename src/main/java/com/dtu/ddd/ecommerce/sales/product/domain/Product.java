package com.dtu.ddd.ecommerce.sales.product.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.joda.money.Money;

@AggregateRoot
public class Product {
  private ProductId id;
  private Title title;
  private Description description;
  private Money price;
  private Quantity quantity;

  void changeTitle(String title) {
    this.title = new Title(title);
  }

  void changeDescription(String description) {
    this.description = new Description(description);
  }

  void changePrice(Money price) {
    if (price.isNegativeOrZero()) {
      throw new IllegalArgumentException("Product's price cannot be less than zero");
    }
    this.price = price;
  }

  void changeQuantity(Quantity quantity) {
    this.quantity = quantity;
  }
}
