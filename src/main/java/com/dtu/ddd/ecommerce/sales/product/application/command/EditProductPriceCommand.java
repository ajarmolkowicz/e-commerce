package com.dtu.ddd.ecommerce.sales.product.application.command;

import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import lombok.Getter;
import org.joda.money.Money;

import java.util.UUID;

public final class EditProductPriceCommand {
    @Getter private final ProductId productId;
    @Getter private final Money price;

    public EditProductPriceCommand(UUID productId, String price) {
        this.productId = new ProductId(productId);
        this.price = Money.parse(price);
    }
}
