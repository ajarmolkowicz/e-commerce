package com.dtu.ddd.ecommerce.sales.product.application;

import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
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
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.dtu.ddd.ecommerce.utils.Assertions.assertCaptureSatisfies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {
  @Mock private ProductRepository productRepository;
  @Mock private OrderRepository orderRepository;
  @Mock private DomainEventPublisher eventPublisher;
  private ProductApplicationService service;

  @BeforeEach
  void setUp() {
    service = new ProductApplicationService(productRepository, orderRepository, eventPublisher);
  }

  @DisplayName("Adding product, should save new product and publish the event")
  @Test
  void addProduct() {
    //GIVEN
    final var command = new AddProductCommand("Harry Potter", "The boy who lived", "EUR 50", 10);

    //WHEN
    service.addProduct(command);

    //THEN
    assertCaptureSatisfies($ -> verify(productRepository).save($.capture()),
        product -> {
          assertThat(product.getTitle()).isEqualTo(new Title(command.title()));
          assertThat(product.getDescription()).isEqualTo(new Description(command.description()));
          assertThat(product.getPrice()).isEqualTo(Money.parse(command.price()));
          assertThat(product.getQuantity()).isEqualTo(new Quantity(command.quantity()));
        }, Product.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> {
          assertThat(event.getProductId()).isNotNull();
          assertThat(event.getTitle()).isEqualTo(new Title(command.title()));
          assertThat(event.getDescription()).isEqualTo(new Description(command.description()));
          assertThat(event.getPrice()).isEqualTo(Money.parse(command.price()));
          assertThat(event.getQuantity()).isEqualTo(new Quantity(command.quantity()));
        }, ProductEvents.ProductAdded.class);
  }

  @DisplayName("Editing product title, should save updated product and publish the event")
  @Test
  void editProductTitle() {
    //GIVEN
    final var product = new Product(new Title("Harry Potter"), new Description("The boy who lived"),
        Money.parse("EUR 50"), new Quantity(10));
    final var command =
        new EditProductTitleCommand(product.getId().id().toString(), "Lord of the rings");
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));

    //WHEN
    service.editProductTitle(command);

    //THEN
    assertCaptureSatisfies($ -> verify(productRepository).save($.capture()),
        persisted -> assertThat(persisted.getTitle()).isEqualTo(new Title(command.title())),
        Product.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getTitle()).isEqualTo(new Title(command.title())),
        ProductEvents.ProductTitleChanged.class);
  }

  @DisplayName("Editing product description, should save updated product and publish the event")
  @Test
  void editProductDescription() {
    //GIVEN
    final var product = new Product(new Title("Harry Potter"), new Description("The boy who lived"),
        Money.parse("EUR 50"), new Quantity(10));
    final var command = new EditProductDescriptionCommand(product.getId().id().toString(),
        "No Dumbledore anymore, now it's Gandalf time");
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));

    //WHEN
    service.editProductDescription(command);

    //THEN
    assertCaptureSatisfies($ -> verify(productRepository).save($.capture()),
        persisted -> assertThat(persisted.getDescription()).isEqualTo(
            new Description(command.description())),
        Product.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getDescription()).isEqualTo(
            new Description(command.description())),
        ProductEvents.ProductDescriptionChanged.class);
  }

  @DisplayName("Editing product price, should save updated product and publish the event")
  @Test
  void editProductPrice() {
    //GIVEN
    final var product = new Product(new Title("Harry Potter"), new Description("The boy who lived"),
        Money.parse("EUR 50"), new Quantity(10));
    final var command = new EditProductPriceCommand(product.getId().id().toString(), "EUR 40");
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));

    //WHEN
    service.editProductPrice(command);

    //THEN
    assertCaptureSatisfies($ -> verify(productRepository).save($.capture()),
        persisted -> assertThat(persisted.getPrice()).isEqualTo(Money.parse("EUR 40")),
        Product.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getPrice()).isEqualTo(Money.parse("EUR 40")),
        ProductEvents.ProductPriceChanged.class);
  }

  @DisplayName("Editing product quantity, should save updated product and publish the event")
  @Test
  void editProductQuantity() {
    //GIVEN
    final var product = new Product(new Title("Harry Potter"), new Description("The boy who lived"),
        Money.parse("EUR 50"), new Quantity(10));
    final var command = new EditProductQuantityCommand(product.getId().id().toString(), 5);
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));

    //WHEN
    service.editProductQuantity(command);

    //THEN
    assertCaptureSatisfies($ -> verify(productRepository).save($.capture()),
        persisted -> assertThat(persisted.getQuantity()).isEqualTo(new Quantity(5)),
        Product.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getQuantity()).isEqualTo(new Quantity(5)),
        ProductEvents.ProductQuantityChanged.class);
  }

  @DisplayName("Deleting product associated with at least one order, should not delete the product")
  @Test
  void deleteProductWithOrders() {
    //GIVEN
    final var product = new Product(new Title("Harry Potter"), new Description("The boy who lived"),
        Money.parse("EUR 50"), new Quantity(10));
    final var command = new DeleteProductCommand(product.getId().id().toString());
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));
    when(orderRepository.findNotDeliveredContainingProduct(product.getId())).thenReturn(
        List.of(new Order(Set.of(new OrderItem(product.getId(), product.getPrice(), new Quantity(1)))))
    );

    //WHEN
    service.deleteProduct(command);

    //THEN
    verify(productRepository).find(product.getId());
    verifyNoMoreInteractions(productRepository);
    verifyNoInteractions(eventPublisher);
  }

  @DisplayName("Deleting product associated with no orders, should product be deleted and event be published")
  @Test
  void deleteProductWithoutOrders() {
    //GIVEN
    final var product = new Product(new Title("Harry Potter"), new Description("The boy who lived"),
        Money.parse("EUR 50"), new Quantity(10));
    final var command = new DeleteProductCommand(product.getId().id().toString());
    when(productRepository.find(product.getId())).thenReturn(Optional.of(product));
    when(orderRepository.findNotDeliveredContainingProduct(product.getId())).thenReturn(new LinkedList<>());

    //WHEN
    service.deleteProduct(command);

    //THEN
    assertCaptureSatisfies($ -> verify(productRepository).delete($.capture()),
        captured -> assertThat(captured.getId()).isEqualTo(product.getId()),
        Product.class);

    //AND THEN
    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getProductId()).isEqualTo(product.getId()),
        ProductEvents.ProductDeleted.class);
  }
}