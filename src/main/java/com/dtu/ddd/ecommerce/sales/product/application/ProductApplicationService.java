package com.dtu.ddd.ecommerce.sales.product.application;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.application.command.AddProductCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.DeleteProductCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductDescriptionCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductPriceCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductQuantityCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductTitleCommand;
import com.dtu.ddd.ecommerce.sales.product.domain.Product;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductEvents;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.cqrs.annotation.CommandHandler;
import org.jmolecules.architecture.hexagonal.PrimaryPort;

import static java.lang.String.format;

@PrimaryPort
@RequiredArgsConstructor
public class ProductApplicationService {
  private final ProductRepository productRepository;
  private final OrderRepository orderRepository;
  private final DomainEventPublisher eventPublisher;

  @CommandHandler
  public ProductId addProduct(AddProductCommand command) {
    final var product = new Product(
        command.getTitle(),
        command.getDescription(),
        command.getPrice(),
        command.getQuantity()
    );

    productRepository.save(product);

    eventPublisher.publish(new ProductEvents.ProductAdded(product));

    return product.getId();
  }

  @CommandHandler
  public void editProductTitle(EditProductTitleCommand command) {
    final var product = productRepository.find(command.getProductId())
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(command.getProductId()));

    product.changeTitle(command.getTitle());
    productRepository.save(product);

    eventPublisher.publish(new ProductEvents.ProductTitleChanged(product.getId(), product.getTitle()));
  }

  @CommandHandler
  public void editProductDescription(EditProductDescriptionCommand command) {
    final var product = productRepository.find(command.getProductId())
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(command.getProductId()));

    product.changeDescription(command.getDescription());
    productRepository.save(product);

    eventPublisher.publish(new ProductEvents.ProductDescriptionChanged(product.getId(), product.getDescription()));
  }

  @CommandHandler
  public void editProductPrice(EditProductPriceCommand command) {
    final var product = productRepository.find(command.getProductId())
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(command.getProductId()));

    product.changePrice(command.getPrice());
    productRepository.save(product);

    eventPublisher.publish(new ProductEvents.ProductPriceChanged(product.getId(), product.getPrice()));
  }

  @CommandHandler
  public void editProductQuantity(EditProductQuantityCommand command) {
    final var product = productRepository.find(command.getProductId())
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(command.getProductId()));

    product.changeQuantity(command.getQuantity());
    productRepository.save(product);

    eventPublisher.publish(new ProductEvents.ProductQuantityChanged(product.getId(), product.getQuantity()));
  }

  @CommandHandler
  public void deleteProduct(DeleteProductCommand command) {
    final var product = productRepository.find(command.getProductId())
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(command.getProductId()));

    if (orderRepository.findNotDeliveredContainingProduct(product.getId()).isEmpty()) {
      productRepository.delete(product);
      eventPublisher.publish(new ProductEvents.ProductDeleted(product.getId()));
    } else {
      throw new Exceptions.ProductContainedInUndeliveredOrders(product.getId());
    }
  }

  public interface Exceptions {
    class ProductContainedInUndeliveredOrders extends BusinessException {
      public ProductContainedInUndeliveredOrders(ProductId id) {
        super(format("Product with id: %s is contained in at least one undelivered order", id.id().toString()));
      }
    }
  }
}
