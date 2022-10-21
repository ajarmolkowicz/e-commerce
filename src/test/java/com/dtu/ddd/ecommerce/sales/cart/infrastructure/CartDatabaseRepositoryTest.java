package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.SalesTestContext;
import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = SalesTestContext.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CartDatabaseRepositoryTest {
  @Autowired
  private CartRepository cartRepository;

  @Test
  void cru() {
    //WHEN
    assertThat(cartRepository.find(CartId.generate())).isEmpty();

    //GIVEN
    final var cart = new Cart();
    cart.add(ProductId.generate(), new Quantity(2));
    cart.add(ProductId.generate(), new Quantity(3));

    //WHEN
    cartRepository.save(cart);

    //THEN
    final var saved = cartRepository.find(cart.getId());
    assertThat(saved).isNotEmpty();
    assertThat(saved.get()).satisfies(
        $ -> assertThat($.getItems()).hasSize(2),
        $ -> assertThat($.getVersion()).isEqualTo(Version.zero()));

    //WHEN
    final var toUpdate = saved.get();
    toUpdate.add(ProductId.generate(), new Quantity(1));
    cartRepository.save(toUpdate);

    //THEN
    final var updated = cartRepository.find(cart.getId());
    assertThat(updated).isNotEmpty();
    assertThat(updated.get()).satisfies(
            $ -> assertThat($.getItems()).hasSize(3),
            $ -> assertThat($.getVersion()).isEqualTo(new Version(1)));

    //GIVEN
    final var updateToSucceed = cartRepository.find(cart.getId()).orElseThrow(RuntimeException::new);
    final var updateToFail = cartRepository.find(cart.getId()).orElseThrow(RuntimeException::new);
    updateToSucceed.add(ProductId.generate(), new Quantity(1));
    updateToFail.add(ProductId.generate(), new Quantity(1));
    updateToFail.add(ProductId.generate(), new Quantity(1));

    //WHEN
    cartRepository.save(updateToSucceed);

    //THEN
    final var updated2 = cartRepository.find(cart.getId());
    assertThat(updated2).isNotEmpty();
    assertThat(updated2.get()).satisfies(
            $ -> assertThat($.getItems()).hasSize(4),
            $ -> assertThat($.getVersion()).isEqualTo(new Version(2)));
    assertThatThrownBy(() -> cartRepository.save(updateToFail)).isInstanceOf(
        CartDatabaseRepository.Exceptions.CartIsStaleException.class);
  }
}