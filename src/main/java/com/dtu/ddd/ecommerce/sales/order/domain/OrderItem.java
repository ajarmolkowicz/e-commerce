package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import javax.validation.constraints.NotNull;
import org.joda.money.Money;

public record OrderItem(@NotNull ProductId productId, @NotNull Money money, @NotNull Quantity quantity) {
}
