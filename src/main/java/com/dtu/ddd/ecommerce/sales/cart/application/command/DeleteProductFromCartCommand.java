package com.dtu.ddd.ecommerce.sales.cart.application.command;

import java.util.UUID;

public record DeleteProductFromCartCommand(UUID cartId, UUID productId) {
}
