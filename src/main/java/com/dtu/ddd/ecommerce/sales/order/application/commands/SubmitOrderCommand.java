package com.dtu.ddd.ecommerce.sales.order.application.commands;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import lombok.Getter;

import java.util.UUID;

public class SubmitOrderCommand {
    @Getter private final CartId cartId;

    public SubmitOrderCommand(UUID cartId) {
        this.cartId = new CartId(cartId);
    }
}

