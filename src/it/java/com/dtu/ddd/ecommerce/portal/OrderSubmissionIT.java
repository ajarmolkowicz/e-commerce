package com.dtu.ddd.ecommerce.portal;

import com.dtu.ddd.ecommerce.billing.domain.CollectionResult;
import com.dtu.ddd.ecommerce.billing.domain.Payment;
import com.dtu.ddd.ecommerce.billing.domain.PaymentProvider;
import com.dtu.ddd.ecommerce.billing.domain.PaymentRepository;
import com.dtu.ddd.ecommerce.billing.domain.ReferenceId;
import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.Order;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import com.dtu.ddd.ecommerce.shipping.domain.Delivery;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryId;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryRepository;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static com.dtu.ddd.ecommerce.utils.Assertions.assertCaptureSatisfies;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderSubmissionIT {
  @Autowired
  private ProductActions productActions;

  @Autowired
  private CartActions cartActions;

  @Autowired
  private OrderActions orderActions;

  @SpyBean
  private OrderRepository orderRepository;

  @SpyBean
  private ProductRepository productRepository;

  @SpyBean
  private PaymentRepository paymentRepository;

  @SpyBean
  private DeliveryRepository deliveryRepository;

  @SpyBean
  private PaymentProvider paymentProvider;

  @DisplayName("Submit order with amount of each product requested being satisfied, should the order be submitted")
  @Test
  void submit_order_enough_products() {
    //GIVEN
    final var bookResponse = productActions.add(
        "Implementing Domain Driven Design",
        "Vaughn Vernon",
        25.0, "EUR", 5);
    assertThat(bookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var bookId = bookResponse.getBody();
    final var movieResponse = productActions.add(
        "Murder on the Orient Express",
        "Work of detective fiction by English writer Agatha Christie",
        25.0, "EUR", 3);
    assertThat(movieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var movieId = movieResponse.getBody();
    final var addBookResponse = cartActions.addProductToCart(TestConfig.cart_id, bookId, 2);
    assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var addMovieResponse = cartActions.addProductToCart(TestConfig.cart_id, movieId, 1);
    assertThat(addMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    //WHEN
    final var result = orderActions.submitOrder(TestConfig.cart_id, "Amager Strandvej 1", "st. th.", "Copenhagen", "2300");

    //THEN
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertCaptureSatisfies($ -> verify(orderRepository).save($.capture()), order -> {
      assertThat(order.getId()).isNotNull();
      assertThat(order.getItems()).containsExactlyInAnyOrder(
          new OrderItem(new ProductId(bookId), Money.parse("EUR 25"), new Quantity(2)),
          new OrderItem(new ProductId(movieId), Money.parse("EUR 25"), new Quantity(1))
      );
      assertThat(order.total()).isEqualTo(Money.parse("EUR 75"));
      assertThat(order.getSubmissionTime()).isNotNull();
    }, Order.class);

    final var book = productRepository.find(new ProductId(bookId)).orElseThrow(RuntimeException::new);
    assertThat(book.getQuantity()).isEqualTo(new Quantity(3));
    final var movie = productRepository.find(new ProductId(movieId)).orElseThrow(RuntimeException::new);
    assertThat(movie.getQuantity()).isEqualTo(new Quantity(2));

    assertCaptureSatisfies($ -> verify(deliveryRepository, times(2)).save($.capture()),
        delivery -> {
          assertThat(delivery.getId()).isNotNull();
          assertThat(delivery.getOrderId()).isNotNull();
          assertThat(delivery.getAddress()).isEqualTo(new Address(
              new Address.Street("Amager Strandvej 1"),
              new Address.HouseNumber("st. th."),
              new Address.City("Copenhagen"),
              new Address.ZipCode("2300")
          ));
          assertThat(delivery.getState()).isEqualTo(Delivery.State.REGISTERED);
        },
        delivery -> assertThat(delivery.getState()).isEqualTo(Delivery.State.IN_DELIVERY), Delivery.class);

    assertCaptureSatisfies($ -> verify(paymentRepository).save($.capture()), payment -> {
      assertThat(payment.getId()).isNotNull();
      assertThat(payment.getCollectionResult()).isEqualTo(new CollectionResult(true));
    }, Payment.class);
  }

  @DisplayName("Submit order while products quantity changed after being added to the cart, should order not be submitted")
  @Test
  void submit_order_not_enough_products() {
    //GIVEN
    final var bookResponse = productActions.add(
        "Implementing Domain Driven Design",
        "Vaughn Vernon",
        25.0, "EUR", 5);
    assertThat(bookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var bookId = bookResponse.getBody();
    final var movieResponse = productActions.add(
        "Murder on the Orient Express",
        "Work of detective fiction by English writer Agatha Christie",
        25.0, "EUR", 3);
    assertThat(movieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var movieId = movieResponse.getBody();
    final var addBookResponse = cartActions.addProductToCart(TestConfig.cart_id, bookId, 5);
    assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var addMovieResponse = cartActions.addProductToCart(TestConfig.cart_id, movieId, 3);
    assertThat(addMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    final var quantityDecreased = productActions.editQuantity(bookId, 4);
    assertThat(quantityDecreased.getStatusCode()).isEqualTo(HttpStatus.OK);

    //WHEN
    final var result = orderActions.submitOrder(
        TestConfig.cart_id, "Amager Strandvej 1", "st. th.", "Copenhagen", "2300"
    );
    //THEN
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(((Map<String, String>) result.getBody()).get("message")).isEqualTo(
        format("Product demand of cart with id: %s is not satisfied", TestConfig.cart_id));
  }

  @DisplayName("Two orders having the same products, no supply for the second one, should the first succeed and the second fail")
  @Test
  void submit_two_orders_first_succeeds_second_fails() {
    //GIVEN
    final var bookResponse = productActions.add(
        "Implementing Domain Driven Design",
        "Vaughn Vernon",
        25.0, "EUR", 5);
    assertThat(bookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var bookId = bookResponse.getBody();
    final var movieResponse = productActions.add(
        "Murder on the Orient Express",
        "Work of detective fiction by English writer Agatha Christie",
        25.0, "EUR", 3);
    assertThat(movieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var movieId = movieResponse.getBody();

    final var addBookResponse1 = cartActions.addProductToCart(TestConfig.cart_1_id, bookId, 4);
    assertThat(addBookResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var addMovieResponse1 = cartActions.addProductToCart(TestConfig.cart_1_id, movieId, 2);
    assertThat(addMovieResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);

    final var addBookResponse2 = cartActions.addProductToCart(TestConfig.cart_2_id, bookId, 2);
    assertThat(addBookResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var addMovieResponse2 = cartActions.addProductToCart(TestConfig.cart_2_id, movieId, 1);
    assertThat(addMovieResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);

    //WHEN
    final var result1 = orderActions.submitOrder(TestConfig.cart_1_id, "Amager Strandvej 1", "st. th.", "Copenhagen", "2300");

    //THEN
    assertThat(result1.getStatusCode()).isEqualTo(HttpStatus.OK);

    assertCaptureSatisfies($ -> verify(orderRepository).save($.capture()), order -> {
      assertThat(order.getId()).isNotNull();
      assertThat(order.getItems()).containsExactlyInAnyOrder(
          new OrderItem(new ProductId(bookId), Money.parse("EUR 25"), new Quantity(4)),
          new OrderItem(new ProductId(movieId), Money.parse("EUR 25"), new Quantity(2))
      );
      assertThat(order.total()).isEqualTo(Money.parse("EUR 150"));
      assertThat(order.getSubmissionTime()).isNotNull();
    }, Order.class);

    final var book = productRepository.find(new ProductId(bookId)).orElseThrow(RuntimeException::new);
    assertThat(book.getQuantity()).isEqualTo(new Quantity(1));
    final var movie = productRepository.find(new ProductId(movieId)).orElseThrow(RuntimeException::new);
    assertThat(movie.getQuantity()).isEqualTo(new Quantity(1));

    //AND WHEN
    final var result2 = orderActions.submitOrder(TestConfig.cart_2_id, "Amager Strandvej 1", "st. th.", "Copenhagen", "2300");

    //THEN
    assertCaptureSatisfies($ -> verify(paymentRepository).save($.capture()),
        payment -> assertThat(payment.getCollectionResult()).isEqualTo(new CollectionResult(true)), Payment.class);

    assertThat(result2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(((Map<String, String>) result2.getBody()).get("message")).isEqualTo(
        format("Product demand of cart with id: %s is not satisfied", TestConfig.cart_2_id));
  }

  @DisplayName("Submit order, payment fails, should product quantity be restored and delivery cancelled")
  @Test
  void submit_order_payment_failed() {
    //GIVEN
    when(paymentProvider.collect(any(ReferenceId.class))).thenReturn(false);
    final var bookResponse = productActions.add(
        "Implementing Domain Driven Design",
        "Vaughn Vernon",
        25.0, "EUR", 5);
    assertThat(bookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var bookId = bookResponse.getBody();
    final var movieResponse = productActions.add(
        "Murder on the Orient Express",
        "Work of detective fiction by English writer Agatha Christie",
        25.0, "EUR", 3);
    assertThat(movieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var movieId = movieResponse.getBody();
    final var addBookResponse = cartActions.addProductToCart(TestConfig.cart_id, bookId, 2);
    assertThat(addBookResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var addMovieResponse = cartActions.addProductToCart(TestConfig.cart_id, movieId, 1);
    assertThat(addMovieResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    //WHEN
    final var result = orderActions.submitOrder(TestConfig.cart_id, "Amager Strandvej 1", "st. th.", "Copenhagen", "2300");

    //THEN
    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(((Map<String, String>) result.getBody()).get("message")).startsWith("Payment for order with id: ").endsWith(" failed");

    final var book = productRepository.find(new ProductId(bookId)).orElseThrow(RuntimeException::new);
    assertThat(book.getQuantity()).isEqualTo(new Quantity(5));
    final var movie = productRepository.find(new ProductId(movieId)).orElseThrow(RuntimeException::new);
    assertThat(movie.getQuantity()).isEqualTo(new Quantity(3));

    assertCaptureSatisfies($ -> verify(paymentRepository).save($.capture()),
        payment -> assertThat(payment.getCollectionResult()).isEqualTo(new CollectionResult(false)), Payment.class);

    verify(deliveryRepository).delete(any(DeliveryId.class));
  }

  @Lazy
  @TestConfiguration
  static class TestConfig {
    @Bean
    CartActions cartActions(TestRestTemplate template, @Value("${local.server.port}") Integer port) {
      return new CartActions(template, port);
    }

    @Bean
    ProductActions productActions(TestRestTemplate template, @Value("${local.server.port}") Integer port) {
      return new ProductActions(template, port);
    }

    @Bean
    OrderActions orderActions(TestRestTemplate template, @Value("${local.server.port}") Integer port) {
      return new OrderActions(template, port);
    }

    static final UUID cart_id = UUID.randomUUID();

    static final UUID cart_1_id = UUID.randomUUID();
    static final UUID cart_2_id = UUID.randomUUID();

    @Bean
    CommandLineRunner initTest(CartRepository cartRepository) {
      return args -> {
        cartRepository.save(new Cart(new CartId(cart_id)));
        cartRepository.save(new Cart(new CartId(cart_1_id)));
        cartRepository.save(new Cart(new CartId(cart_2_id)));
      };
    }

    @Bean
    TestRestTemplate template() {
      return new TestRestTemplate();
    }
  }
}
