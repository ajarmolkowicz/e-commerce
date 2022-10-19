package com.dtu.ddd.ecommerce.sales.product.application.command;

import com.dtu.ddd.ecommerce.sales.product.domain.Description;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import lombok.Getter;

import java.util.UUID;

public final class EditProductDescriptionCommand {
    @Getter private final ProductId productId;
    @Getter private final Description description;

    public EditProductDescriptionCommand(UUID productId, String description) {
        this.productId = new ProductId(productId);
        this.description = new Description(description);
    }
}
