package com.dtu.ddd.ecommerce.sales.product.domain;

import com.dtu.ddd.ecommerce.shared.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;
import lombok.Value;
import org.joda.money.Money;

public interface ProductEvents extends DomainEvent {
  @Value
  class ProductAdded implements ProductEvents {
    UUID eventId;
    Instant when;
    ProductId productId;
    Title title;
    Description description;
    Money price;
    Quantity quantity;

    public ProductAdded(Product product) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.productId = product.getId();
      this.title = product.getTitle();
      this.description = product.getDescription();
      this.price = product.getPrice();
      this.quantity = product.getQuantity();
    }
  }

  @Value
  class ProductDeleted implements ProductEvents {
    UUID eventId;
    Instant when;
    ProductId productId;

    public ProductDeleted(ProductId productId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.productId = productId;
    }
  }

  @Value
  class ProductArchived implements ProductEvents {
    UUID eventId;
    Instant when;
    ProductId productId;

    public ProductArchived(ProductId productId) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.productId = productId;
    }
  }

  @Value
  class ProductTitleChanged implements ProductEvents {
    UUID eventId;
    Instant when;
    ProductId productId;
    Title title;

    public ProductTitleChanged(ProductId productId, Title title) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.productId = productId;
      this.title = title;
    }
  }

  @Value
  class ProductDescriptionChanged implements ProductEvents {
    UUID eventId;
    Instant when;
    ProductId productId;
    Description description;

    public ProductDescriptionChanged(ProductId productId, Description description) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.productId = productId;
      this.description = description;
    }
  }

  @Value
  class ProductPriceChanged implements ProductEvents {
    UUID eventId;
    Instant when;
    ProductId productId;
    Money price;

    public ProductPriceChanged(ProductId productId, Money price) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.productId = productId;
      this.price = price;
    }
  }

  @Value
  class ProductQuantityChanged implements ProductEvents {
    UUID eventId;
    Instant when;
    ProductId productId;
    Quantity quantity;

    public ProductQuantityChanged(ProductId productId, Quantity quantity) {
      this.eventId = UUID.randomUUID();
      this.when = Instant.now();
      this.productId = productId;
      this.quantity = quantity;
    }
  }
}
