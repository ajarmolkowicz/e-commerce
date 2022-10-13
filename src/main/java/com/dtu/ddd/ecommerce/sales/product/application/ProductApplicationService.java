package com.dtu.ddd.ecommerce.sales.product.application;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.application.command.AddProductCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.DeleteProductCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductDescriptionCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductPriceCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductQuantityCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductTitleCommand;
import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.Product;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductEvents;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.joda.money.Money;

@RequiredArgsConstructor
class ProductApplicationService {
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final DomainEventPublisher eventPublisher;

  void addProduct(AddProductCommand command) {
    final var product = new Product(
        new Title(command.title()),
        new Description(command.description()),
        Money.parse(command.price()),
        new Quantity(command.quantity())
    );

    productRepository.save(product);

    eventPublisher.publish(new ProductEvents.ProductAdded(product));
  }

  void editProductTitle(EditProductTitleCommand command) {
    final var id = new ProductId(UUID.fromString(command.productId()));
    final var product = productRepository.find(id)
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(id));

    product.changeTitle(command.title());
    productRepository.save(product);

    eventPublisher.publish(
        new ProductEvents.ProductTitleChanged(product.getId(), product.getTitle()));
  }

  void editProductDescription(EditProductDescriptionCommand command) {
    final var id = new ProductId(UUID.fromString(command.productId()));
    final var product = productRepository.find(id)
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(id));

    product.changeDescription(command.description());
    productRepository.save(product);

    eventPublisher.publish(
        new ProductEvents.ProductDescriptionChanged(product.getId(), product.getDescription()));
  }

  void editProductPrice(EditProductPriceCommand command) {
    final var id = new ProductId(UUID.fromString(command.productId()));
    final var product = productRepository.find(id)
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(id));

    product.changePrice(Money.parse(command.money()));
    productRepository.save(product);

    eventPublisher.publish(
        new ProductEvents.ProductPriceChanged(product.getId(), product.getPrice()));
  }

  void editProductQuantity(EditProductQuantityCommand command) {
    final var id = new ProductId(UUID.fromString(command.productId()));
    final var product = productRepository.find(id)
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(id));

    product.changeQuantity(new Quantity(command.quantity()));
    productRepository.save(product);

    eventPublisher.publish(
        new ProductEvents.ProductQuantityChanged(product.getId(), product.getQuantity()));
  }

  void deleteProduct(DeleteProductCommand command) {
    final var id = new ProductId(UUID.fromString(command.productId()));
    final var product = productRepository.find(id)
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(id));

    if (orderRepository.findNotDeliveredContainingProduct(product.getId()).isEmpty()) {
      productRepository.delete(product);
      eventPublisher.publish(new ProductEvents.ProductDeleted(product.getId()));
    }
  }
}
