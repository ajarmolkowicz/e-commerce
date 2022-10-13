package com.dtu.ddd.ecommerce.sales.order.application.commands;

import java.util.UUID;

public record SubmitOrderCommand(UUID cartId) {
}
