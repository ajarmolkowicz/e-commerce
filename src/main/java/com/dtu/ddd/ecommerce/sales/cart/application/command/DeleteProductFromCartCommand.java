package com.dtu.ddd.ecommerce.sales.cart.application.command;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import lombok.Getter;

import java.util.UUID;

public final class DeleteProductFromCartCommand {
    @Getter private final CartId cartId;
    @Getter private final ProductId productId;

    public DeleteProductFromCartCommand(UUID cartId, UUID productId) {
        this.cartId = new CartId(cartId);
        this.productId = new ProductId(productId);
    }
}
