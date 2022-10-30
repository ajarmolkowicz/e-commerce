package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {
  @DisplayName("Adding a product when there is already one, should throw CartAlreadyContainsProductException")
  @Test
  void productAlreadyAdded() {
    //GIVEN
    final var cart = new Cart();
    final var productId = ProductId.generate();
    cart.add(productId, new Quantity(1));

    assertThatThrownBy(
        //WHEN
        () -> cart.add(productId, new Quantity(1))
    )
        //THEN
        .isInstanceOf(Cart.Exceptions.CartAlreadyContainsProductException.class);
  }

  @DisplayName("Deleting a product that is not on the item list, should throw CartDoesNotContainProductException")
  @Test
  void productNotOnTheItemList() {
    //GIVEN
    final var cart = new Cart();
    final var productId = ProductId.generate();

    assertThatThrownBy(
        //WHEN
        () -> cart.delete(productId)
    )
        //THEN
        .isInstanceOf(Cart.Exceptions.CartDoesNotContainProductException.class);
  }

  @DisplayName("Manipulating cart items outside cart, should throw Exception")
  @Test
  void manageItemsOutsideCart() {
    //GIVEN
    final var cart = new Cart();
    cart.add(ProductId.generate(), new Quantity(1));
    cart.add(ProductId.generate(), new Quantity(2));
    cart.add(ProductId.generate(), new Quantity(3));

    assertThatThrownBy(
        //WHEN
        () -> cart.getItems().clear()
    )
        //THEN
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @DisplayName("Clearing cart, should cart items be empty")
  @Test
  void clear() {
    //GIVEN
    final var cart = new Cart();
    cart.add(ProductId.generate(), new Quantity(1));
    cart.add(ProductId.generate(), new Quantity(2));
    cart.add(ProductId.generate(), new Quantity(3));
    assertThat(cart.getItems()).hasSize(3);

    //WHEN
    cart.clear();

    //THEN
    assertThat(cart.getItems()).isEmpty();
  }
}