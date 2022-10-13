package com.dtu.ddd.ecommerce.sales.product.application.command;

public record AddProductCommand(String title, String description, String price, Integer quantity) {
}
