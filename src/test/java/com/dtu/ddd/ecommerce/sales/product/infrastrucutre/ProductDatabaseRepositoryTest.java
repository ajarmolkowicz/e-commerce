package com.dtu.ddd.ecommerce.sales.product.infrastrucutre;

import com.dtu.ddd.ecommerce.sales.SalesTestContext;
import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.Product;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.sales.product.domain.Title;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = SalesTestContext.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ProductDatabaseRepositoryTest {
  @Autowired
  private ProductRepository productRepository;

  @Test
  void crud() {
    //WHEN
    assertThat(productRepository.find(ProductId.generate())).isEmpty();

    //GIVEN
    final var product = new Product(
        new Title("Harry Potter"),
        new Description("The boy who lived"),
        Money.parse("EUR 50"),
        new Quantity(10));

    //WHEN
    productRepository.save(product);

    //THEN
    final var saved = productRepository.find(product.getId());
    assertThat(saved).isNotEmpty();
    assertThat(saved.get()).satisfies(
        $ -> assertThat($.getTitle()).isEqualTo(new Title("Harry Potter")),
        $ -> assertThat($.getDescription()).isEqualTo(new Description("The boy who lived")),
        $ -> assertThat($.getPrice()).isEqualTo(Money.parse("EUR 50")),
        $ -> assertThat($.getQuantity()).isEqualTo(new Quantity(10)),
        $ -> assertThat($.getVersion()).isEqualTo(Version.zero()));

    //WHEN
    final var toUpdate = saved.get();
    toUpdate.changeTitle(new Title("Lord of the rings"));
    productRepository.save(toUpdate);

    //THEN
    final var updated = productRepository.find(product.getId()).orElseThrow(RuntimeException::new);
    assertThat(updated.getTitle()).isEqualTo(new Title("Lord of the rings"));
    assertThat(updated.getVersion()).isEqualTo(new Version(1));

    //GIVEN
    final var toUpdateSuccess =
        productRepository.find(product.getId()).orElseThrow(RuntimeException::new);
    final var toUpdateFail =
        productRepository.find(product.getId()).orElseThrow(RuntimeException::new);

    //WHEN
    toUpdateSuccess.changeQuantity(new Quantity(5));
    toUpdateFail.changePrice(Money.parse("EUR 35"));
    productRepository.save(toUpdateSuccess);
    assertThatThrownBy(() -> productRepository.save(toUpdateFail)).isInstanceOf(
        ProductDatabaseRepository.Exceptions.ProductIsStaleException.class);

    //WHEN
    final var toDelete = productRepository.find(product.getId()).orElseThrow(RuntimeException::new);
    productRepository.delete(toDelete);

    //THEN
    assertThat(productRepository.find(product.getId())).isEmpty();
  }
}