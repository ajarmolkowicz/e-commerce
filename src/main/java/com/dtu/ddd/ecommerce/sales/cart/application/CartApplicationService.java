package com.dtu.ddd.ecommerce.sales.cart.application;

import com.dtu.ddd.ecommerce.sales.cart.application.command.AddProductToCartCommand;
import com.dtu.ddd.ecommerce.sales.cart.application.command.DeleteProductFromCartCommand;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartEvents;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
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
public class CartApplicationService {
  private final CartRepository cartRepository;
  private final ProductRepository productRepository;
  private final DomainEventPublisher eventPublisher;

  @CommandHandler
  public void addProductToCart(AddProductToCartCommand command) {
    final var cart = cartRepository.find(command.getCartId())
        .orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(command.getCartId()));
    final var product = productRepository.find(command.getProductId())
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound(command.getProductId()));

    if (!product.enoughInStock(command.getQuantity())) {
      throw new Exceptions.InsufficientProductInStock(product.getId());
    }

    cart.add(command.getProductId(), command.getQuantity());
    cartRepository.save(cart);

    eventPublisher.publish(new CartEvents.ProductAddedToCart(cart.getId(), command.getProductId(), command.getQuantity()));
  }

  @CommandHandler
  public void deleteProductFromCart(DeleteProductFromCartCommand command) {
    final var cart = cartRepository.find(command.getCartId())
        .orElseThrow(() -> new CartRepository.Exceptions.CartNotFound(command.getCartId()));

    cart.delete(command.getProductId());
    cartRepository.save(cart);

    eventPublisher.publish(new CartEvents.ProductDeletedFromCart(cart.getId(), command.getProductId()));
  }

  public interface Exceptions {
    class InsufficientProductInStock extends BusinessException {
      public InsufficientProductInStock(ProductId id) {
        super(format("Product with id: %s quantity in stock is insufficient", id.id().toString()));
      }
    }
  }
}
