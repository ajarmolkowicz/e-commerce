package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartItem;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartItemId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@NoArgsConstructor
class CartItemDatabaseEntity {
    @Id
    Long id;
    UUID cart_item_id;
    UUID cart_id;
    UUID product_id;
    Integer quantity;

    public CartItemDatabaseEntity(Long id, UUID cart_item_id, UUID cart_id, UUID product_id, Integer quantity) {
        this.id = id;
        this.cart_item_id = cart_item_id;
        this.cart_id = cart_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    CartItem toDomainModel() {
        return new CartItem(
          new CartItemId(cart_item_id),
          new ProductId(product_id),
          new Quantity(quantity)
        );
    }
}
