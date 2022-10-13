package com.dtu.ddd.ecommerce.sales.product.application.command;

public record EditProductPriceCommand(String productId, String money) {
}
