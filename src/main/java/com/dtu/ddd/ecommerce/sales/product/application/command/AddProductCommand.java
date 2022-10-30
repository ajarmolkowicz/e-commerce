package com.dtu.ddd.ecommerce.sales.product.application.command;

import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import lombok.Getter;
import org.jmolecules.architecture.cqrs.annotation.Command;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

@Command
public final class AddProductCommand {
  @Getter private final Title title;
  @Getter private final Description description;
  @Getter private final Money price;
  @Getter private final Quantity quantity;

  public AddProductCommand(String title, String description, Double price, String currency, Integer quantity) {
    this.title = new Title(title);
    this.description = new Description(description);
    this.price = Money.of(CurrencyUnit.of(currency), price);
    this.quantity = new Quantity(quantity);
  }
}
