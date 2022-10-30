package com.dtu.ddd.ecommerce.sales.product.application.command;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import lombok.Getter;

import java.util.UUID;
import org.jmolecules.architecture.cqrs.annotation.Command;

@Command
public final class EditProductTitleCommand {
  @Getter private final ProductId productId;
  @Getter private final Title title;

  public EditProductTitleCommand(UUID productId, String title) {
    this.productId = new ProductId(productId);
    this.title = new Title(title);
  }
}
