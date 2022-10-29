package com.dtu.ddd.ecommerce.sales.product.domain;

import org.joda.money.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.valid4j.errors.RequireViolation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {
  @DisplayName("Negative price, should throw require violation exception")
  @Test
  void negativePrice() {
    //GIVEN
    final var product = new Product(
        new Title("Book"),
        new Description("Adventure book"),
        Money.parse("EUR 20"),
        Quantity.ONE);

    assertThatThrownBy(
        //WHEN
        () -> product.changePrice(Money.parse("EUR -20"))
    )
        //THEN
        .isInstanceOf(RequireViolation.class)
        .hasMessage("Price must be positive");
  }

  @DisplayName("Product ordered, should decrease product quantity")
  @Test
  void ordered() {
    //GIVEN
    final var product = new Product(
        new Title("Book"),
        new Description("Adventure book"),
        Money.parse("EUR 20"),
        Quantity.ONE);

    //WHEN
    product.ordered(Quantity.ONE);

    //THEN
    assertThat(product.getQuantity()).isEqualTo(Quantity.ZERO);
  }

  @DisplayName("Product ordered with too high demand quantity, should throw an exception")
  @Test
  void orderedTooMany() {
    //GIVEN
    final var product = new Product(
        new Title("Book"),
        new Description("Adventure book"),
        Money.parse("EUR 20"),
        Quantity.ONE);

    assertThatThrownBy(
        //WHEN
        () -> product.ordered(new Quantity(2))
    )
        //THEN
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Quantity cannot be less than zero");
  }
}
