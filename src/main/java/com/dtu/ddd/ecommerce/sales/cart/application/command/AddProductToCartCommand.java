package com.dtu.ddd.ecommerce.sales.cart.application.command;

import java.util.UUID;

public record AddProductToCartCommand(UUID cartId, UUID productId, Integer quantity) {
}
