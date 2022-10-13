package com.dtu.ddd.ecommerce.sales.cart.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {
  @DisplayName("Adding a product when there is already one, should throw CartAlreadyContainsProductException")
  @Test
  void productAlreadyAdded() {
    //GIVEN
    final var cart = new Cart();
    final var productId = new ProductId(UUID.randomUUID());
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
    final var productId = new ProductId(UUID.randomUUID());

    assertThatThrownBy(
        //WHEN
        () -> cart.delete(productId)
    )
        //THEN
        .isInstanceOf(Cart.Exceptions.CartDoesNotContainProductException.class);
  }
}