package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItem;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.Product;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
  @Mock private ProductRepository productRepository;
  private OrderService orderService;

  @BeforeEach
  void setUp() {
    orderService = new OrderService(productRepository);
  }

  @DisplayName("Products quantity is {3 ; 1}, product demand {2 ; 1} should be satisfied")
  @Test
  void enoughProductsForAnOrder() {
    //GIVEN
    final var product1 =  new Product(new Title("Book 1"), new Description("Adventure book"), Money.parse("EUR 20"), new Quantity(3));
    final var product2 =  new Product(new Title("Book 2"), new Description("Fantasy book"), Money.parse("EUR 50"), new Quantity(1));
    final var cart = new Cart();
    cart.add(product1.getId(), new Quantity(2)); cart.add(product2.getId(), new Quantity(1));
    when(productRepository.find(product1.getId())).thenReturn(Optional.of(product1));
    when(productRepository.find(product2.getId())).thenReturn(Optional.of(product2));

    //WHEN
    final var enough = orderService.enoughProductsForAnOrder(cart);

    //THEN
    assertThat(enough).isTrue();
  }

  @DisplayName("Products quantity is {1}, product demand {2} should not be satisfied")
  @Test
  void notEnoughProductsForAnOrder() {
    //GIVEN
    final var product1 =  new Product(new Title("Book 1"), new Description("Adventure book"), Money.parse("EUR 20"), new Quantity(1));
    final var cart = new Cart();
    cart.add(product1.getId(), new Quantity(2));
    when(productRepository.find(product1.getId())).thenReturn(Optional.of(product1));

    //WHEN
    final var enough = orderService.enoughProductsForAnOrder(cart);

    //THEN
    assertThat(enough).isFalse();
  }

  @Test
  void orderItemPricesAssigned() {
    //GIVEN
    final var product1 =  new Product(new Title("Book 1"), new Description("Adventure book"), Money.parse("EUR 20"), new Quantity(3));
    final var product2 =  new Product(new Title("Book 2"), new Description("Fantasy book"), Money.parse("EUR 50"), new Quantity(1));
    final var cart = new Cart();
    cart.add(product1.getId(), new Quantity(2)); cart.add(product2.getId(), new Quantity(2));
    when(productRepository.find(product1.getId())).thenReturn(Optional.of(product1));
    when(productRepository.find(product2.getId())).thenReturn(Optional.of(product2));

    //WHEN
    final var orderItems = orderService.assignPricesToItems(Set.of(
        new CartItem(product1.getId(), new Quantity(2)),
        new CartItem(product2.getId(), new Quantity(1))
    ));

    //THEN
    assertThat(orderItems).containsExactlyInAnyOrder(
        new OrderItem(product1.getId(), Money.parse("EUR 20"), new Quantity(2)),
        new OrderItem(product2.getId(), Money.parse("EUR 50"), new Quantity(1))
    );
  }
}