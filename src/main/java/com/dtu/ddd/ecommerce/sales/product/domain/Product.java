package com.dtu.ddd.ecommerce.sales.product.domain;

import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import lombok.Getter;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.joda.money.Money;

import static org.valid4j.Assertive.require;

@AggregateRoot
public class Product {
  @Getter private final ProductId id;
  @Getter private Title title;
  @Getter private Description description;
  @Getter private Money price;
  @Getter private Quantity quantity;
  @Getter private Version version;
  public Product(ProductId id, Title title, Description description, Money price, Quantity quantity, Version version) {
    require(id != null, "Id cannot be null");
    require(title != null, "Title cannot be null");
    require(description != null, "Description cannot be null");
    require(price != null && price.isPositive(), "Price must be positive");
    require(quantity.isPositiveOrZero(), "Quantity cannot be negative");
    this.id = id;
    this.title = title;
    this.description = description;
    this.price = price;
    this.quantity = quantity;
    this.version = version;
  }

  public Product(Title title, Description description, Money price, Quantity quantity) {
    require(title != null, "Title cannot be null");
    require(description != null, "Description cannot be null");
    require(price != null && price.isPositive(), "Price must be positive");
    require(quantity.isPositiveOrZero(), "Quantity cannot be negative");
    this.id = ProductId.generate();
    this.title = title;
    this.description = description;
    this.price = price;
    this.quantity = quantity;
  }

  public void changeTitle(Title title) {
    this.title = title;
  }

  public void changeDescription(Description description) {
    this.description = description;
  }

  public void changePrice(Money price) {
    require(price.isPositive(), "Price must be positive");
    this.price = price;
  }

  public void changeQuantity(Quantity quantity) {
    this.quantity = quantity;
  }

  public Boolean orderableForGivenQuantity(Quantity quantity) {
    return this.quantity.isGreaterOrEqual(quantity);
  }
}
