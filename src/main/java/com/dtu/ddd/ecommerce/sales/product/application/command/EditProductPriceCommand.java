package com.dtu.ddd.ecommerce.sales.product.application.command;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import lombok.Getter;
import org.jmolecules.architecture.cqrs.annotation.Command;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.UUID;

@Command
public final class EditProductPriceCommand {
  @Getter private final ProductId productId;
  @Getter private final Money price;

  public EditProductPriceCommand(UUID productId, Double price, String currency) {
    this.productId = new ProductId(productId);
    this.price = Money.of(CurrencyUnit.of(currency), price);
  }
}
