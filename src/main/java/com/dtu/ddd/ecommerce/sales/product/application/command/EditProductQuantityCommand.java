package com.dtu.ddd.ecommerce.sales.product.application.command;

public record EditProductQuantityCommand(String productId, Integer quantity) {
}
