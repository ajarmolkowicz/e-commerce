package com.dtu.ddd.ecommerce.sales.order.domain;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import org.joda.money.Money;

public record OrderItem(ProductId productId, Money money, Quantity quantity) {
}
