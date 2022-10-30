package com.dtu.ddd.ecommerce.portal;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import java.util.Map;
import java.util.UUID;
import org.joda.money.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CartManagementIT {
  @Autowired
  private ProductActions productActions;

  @Autowired
  private CartActions cartActions;

  @DisplayName("Add the same product to the cart twice, should the second request fail")
  @Test
  void add_same_product_twice() {
    //GIVEN
    final var bookResponse = productActions.add(
        "Implementing Domain Driven Design",
        "Vaughn Vernon",
        25.0, "EUR", 5);
    assertThat(bookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var bookId = bookResponse.getBody();

    //WHEN
    final var response1 = cartActions.addProductToCart(TestConfig.cart_id, bookId, 2);

    //THEN
    assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

    //AND WHEN
    final var response2 = cartActions.addProductToCart(TestConfig.cart_id, bookId, 2);
    assertThat(((Map<String, String>) response2.getBody()).get("message")).isEqualTo(
        format("Cart with id: %s already contains product with id: %s", TestConfig.cart_id, bookId));
  }

  @DisplayName("Add product with insufficient stock supply, should product not be added")
  @Test
  void add_product_insufficient_stock_supply() {
    //GIVEN
    final var bookResponse = productActions.add(
        "Implementing Domain Driven Design",
        "Vaughn Vernon",
        25.0, "EUR", 5);
    assertThat(bookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var bookId = bookResponse.getBody();

    //WHEN
    final var response = cartActions.addProductToCart(TestConfig.cart_id, bookId, 10);

    //THEN
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(((Map<String, String>) response.getBody()).get("message")).isEqualTo(
        format("Product with id: %s quantity in stock is insufficient", bookId));
  }

  @Lazy
  @TestConfiguration
  static class TestConfig {
    static final UUID cart_id = UUID.randomUUID();

    @Bean
    CommandLineRunner initTest(CartRepository cartRepository) {
      return args -> cartRepository.save(new Cart(new CartId(cart_id)));
    }

    @Bean
    public TestRestTemplate template() {
      return new TestRestTemplate();
    }

    @Bean
    ProductActions productActions(TestRestTemplate template, @Value("${local.server.port}") Integer port) {
      return new ProductActions(template, port);
    }

    @Bean
    CartActions cartActions(TestRestTemplate template, @Value("${local.server.port}") Integer port) {
      return new CartActions(template, port);
    }
  }
}
