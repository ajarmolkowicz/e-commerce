package com.dtu.ddd.ecommerce.portal;

import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import org.joda.money.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductManagementIT {
  @Autowired
  private ProductActions productActions;

  @Autowired
  private ProductRepository productRepository;

  @DisplayName("Create product, edit its properties, then delete, should all changes be reflected in the database")
  @Test
  void crud() {
    final var created = productActions.add("Harry Potter",
        "The boy who lived",
        100.0,
        "EUR",
        1);

    assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);
    final var id = created.getBody();
    var product = productRepository.find(new ProductId(id));
    assertThat(product).isNotEmpty();
    assertThat(product.get()).satisfies(
        $ -> assertThat($.getTitle()).isEqualTo(new Title("Harry Potter")),
        $ -> assertThat($.getDescription()).isEqualTo(new Description("The boy who lived")),
        $ -> assertThat($.getPrice()).isEqualTo(Money.parse("EUR 100")),
        $ -> assertThat($.getQuantity()).isEqualTo(new Quantity(1))
    );

    final var titleUpdated = productActions.editTitle(id, "Lord of the rings");

    assertThat(titleUpdated.getStatusCode()).isEqualTo(HttpStatus.OK);
    product = productRepository.find(new ProductId(id));
    assertThat(product).isNotEmpty();
    assertThat(product.get()).satisfies($ -> assertThat($.getTitle()).isEqualTo(new Title("Lord of the rings")));

    final var descriptionUpdated = productActions.editDescription(id, "Epic high-fantasy novel by J. R. R. Tolkien");

    assertThat(descriptionUpdated.getStatusCode()).isEqualTo(HttpStatus.OK);
    product = productRepository.find(new ProductId(id));
    assertThat(product).isNotEmpty();
    assertThat(product.get()).satisfies(
        $ -> assertThat($.getDescription()).isEqualTo(new Description("Epic high-fantasy novel by J. R. R. Tolkien")));

    final var priceUpdated = productActions.editPrice(id, 50.0, "EUR");

    assertThat(priceUpdated.getStatusCode()).isEqualTo(HttpStatus.OK);
    product = productRepository.find(new ProductId(id));
    assertThat(product).isNotEmpty();
    assertThat(product.get()).satisfies($ -> assertThat($.getPrice()).isEqualTo(Money.parse("EUR 50")));

    final var quantityUpdated = productActions.editQuantity(id, 2);

    assertThat(quantityUpdated.getStatusCode()).isEqualTo(HttpStatus.OK);
    product = productRepository.find(new ProductId(id));
    assertThat(product).isNotEmpty();
    assertThat(product.get()).satisfies($ -> assertThat($.getQuantity()).isEqualTo(new Quantity(2)));

    final var deleted = productActions.delete(id);

    assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(productRepository.find(new ProductId(id))).isEmpty();
  }

  @Lazy
  @TestConfiguration
  static class TestConfig {
    @Bean
    public TestRestTemplate template() {
      return new TestRestTemplate();
    }

    @Bean
    ProductActions productActions(TestRestTemplate template, @Value("${local.server.port}") Integer port) {
      return new ProductActions(template, port);
    }
  }
}
