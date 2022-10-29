package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
class CartDatabaseEntity {
  @Setter UUID cart_id;
  @Setter Integer version;

  Cart toDomainModel(Collection<CartItemDatabaseEntity> cartItemDatabaseEntities) {
    return new Cart(
        new CartId(cart_id),
        cartItemDatabaseEntities.stream().map(CartItemDatabaseEntity::toDomainModel).collect(Collectors.toSet()),
        new Version(version)
    );
  }
}
