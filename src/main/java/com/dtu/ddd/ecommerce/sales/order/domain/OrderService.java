package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItem;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderService {
  private final ProductRepository productRepository;

  public Boolean enoughProductsForAnOrder(Cart cart) {
    return cart.getItems().stream().allMatch($ -> productRepository
        .find($.getProductId())
        .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound($.getProductId()))
        .orderableForGivenQuantity($.getQuantity()));
  }

  public Set<OrderItem> assignPricesToItems(Set<CartItem> items) {
    return items.stream().map($ -> new OrderItem(
        $.getProductId(),
        productRepository
            .find($.getProductId())
            .orElseThrow(() -> new ProductRepository.Exceptions.ProductNotFound($.getProductId()))
            .getPrice(),
        $.getQuantity()
    )).collect(Collectors.toSet());
  }
}
