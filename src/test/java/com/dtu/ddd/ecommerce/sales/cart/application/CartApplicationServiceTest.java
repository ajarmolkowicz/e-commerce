package com.dtu.ddd.ecommerce.sales.cart.application;

import com.dtu.ddd.ecommerce.sales.cart.application.command.AddProductToCartCommand;
import com.dtu.ddd.ecommerce.sales.cart.application.command.DeleteProductFromCartCommand;
import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartEvents;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.Product;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import java.util.Optional;
import java.util.UUID;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.dtu.ddd.ecommerce.utils.Assertions.assertCaptureSatisfies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartApplicationServiceTest {
  @Mock private CartRepository cartRepository;
  @Mock private ProductRepository productRepository;
  @Mock private DomainEventPublisher eventPublisher;
  private CartApplicationService service;

  @BeforeEach
  void setUp() {
    service = new CartApplicationService(cartRepository, productRepository, eventPublisher);
  }

  @DisplayName("Adding product to cart, should add product to items set with appropriate quantity")
  @Test
  void addProductToCart() {
    //GIVEN
    final var cart = new Cart();
    final var product = new Product(new Title("Book"), new Description("Adventure book"), Money.parse("EUR 20"), Quantity.ONE);
    final var command = new AddProductToCartCommand(cart.getId().id(), product.getId().id(), 1);
    when(cartRepository.find(cart.getId())).thenReturn(Optional.of(cart));
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));

    //WHEN
    service.addProductToCart(command);

    //THEN
    assertCaptureSatisfies($ -> verify(cartRepository).save($.capture()),
        event -> {
          assertThat(cart.getItems())
              .filteredOn($ -> $.getProductId().equals(product.getId())).hasSize(1);
          assertThat(cart.getItems().stream().findAny().orElseThrow(RuntimeException::new))
              .satisfies($ -> assertThat($.getProductId()).isEqualTo(product.getId()),
                  $ -> assertThat($.getQuantity()).isEqualTo(new Quantity(1)));
        },
        Cart.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> {
          assertThat(event.getCartId()).isEqualTo(cart.getId());
          assertThat(event.getProductId()).isEqualTo(product.getId());
          assertThat(event.getQuantity()).isEqualTo(new Quantity(1));
        },
        CartEvents.ProductAddedToCart.class);
  }

  @DisplayName("Adding product to cart with insufficient stock supply, should throw InsufficientProductInStock exception")
  @Test
  void addInsufficientStockSupplyProductToCart() {
    //GIVEN
    final var cart = new Cart();
    final var product = new Product(new Title("Book"), new Description("Adventure book"), Money.parse("EUR 20"), Quantity.ONE);
    final var command = new AddProductToCartCommand(cart.getId().id(), product.getId().id(), 2);
    when(cartRepository.find(cart.getId())).thenReturn(Optional.of(cart));
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));

    //WHEN
    assertThatThrownBy(
        //WHEN
        () -> service.addProductToCart(command)
    )
        //THEN
        .isInstanceOf(CartApplicationService.Exceptions.InsufficientProductInStock.class);
  }

  @DisplayName("Deleting product from cart, should remove product from items set")
  @Test
  void deleteProductToCart() {
    //GIVEN
    final var cart = new Cart();
    final var productId = new ProductId(UUID.randomUUID());
    cart.add(productId, new Quantity(1));
    final var command = new DeleteProductFromCartCommand(cart.getId().id(), productId.id());
    when(cartRepository.find(cart.getId())).thenReturn(Optional.of(cart));

    //WHEN
    service.deleteProductFromCart(command);

    //THEN
    assertCaptureSatisfies($ -> verify(cartRepository).save($.capture()),
        event -> assertThat(cart.getItems())
            .filteredOn($ -> $.getProductId().equals(productId)).isEmpty(),
        Cart.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> {
          assertThat(event.getCartId()).isEqualTo(cart.getId());
          assertThat(event.getProductId()).isEqualTo(productId);
        },
        CartEvents.ProductDeletedFromCart.class);
  }
}