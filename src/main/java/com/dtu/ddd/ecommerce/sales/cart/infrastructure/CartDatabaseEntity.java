package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItem;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItemId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

import java.util.*;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
class CartDatabaseEntity {
  @Id
  Long id;
  UUID cart_id;
  Set<CartDatabaseEntity> items;
  Integer version;

  public CartDatabaseEntity(UUID id, Set<CartDatabaseEntity> items, Integer version) {
    this.cart_id = id;
    this.items = items;
    this.version = version;
  }

  Cart toDomainModel(Collection<CartItemDatabaseEntity> cartItemDatabaseEntities) {
    return new Cart(
        new CartId(cart_id),
        cartItemDatabaseEntities.stream().map(CartItemDatabaseEntity::toDomainModel).collect(Collectors.toSet()),
        new Version(version)
    );
  }
}
