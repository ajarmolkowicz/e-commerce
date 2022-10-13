package com.dtu.ddd.ecommerce.sales.product.infrastrucutre;

import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.Product;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.Money;

@NoArgsConstructor
class ProductDatabaseEntity {
  @Setter UUID id;
  @Setter String title;
  @Setter String description;
  @Setter String price;
  @Setter Integer quantity;
  @Setter Status status;
  @Setter Integer version;

  enum Status {
    USABLE, DELETED
  }

  Boolean isUsable() {
    return status == Status.USABLE;
  }

  public Product toDomainModel() {
    return new Product(
        new ProductId(id),
        new Title(title),
        new Description(description),
        Money.parse(price),
        new Quantity(quantity),
        new Version(version)
    );
  }
}
