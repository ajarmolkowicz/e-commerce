package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartItem;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItemId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
class CartItemDatabaseEntity {
    @Setter UUID cart_item_id;
    @Setter UUID cart_id;
    @Setter UUID product_id;
    @Setter Integer quantity;

    CartItem toDomainModel() {
        return new CartItem(
          new CartItemId(cart_item_id),
          new ProductId(product_id),
          new Quantity(quantity)
        );
    }
}
