package com.dtu.ddd.ecommerce.sales.product.application.command;

public record EditProductDescriptionCommand(String productId, String description) {
}
