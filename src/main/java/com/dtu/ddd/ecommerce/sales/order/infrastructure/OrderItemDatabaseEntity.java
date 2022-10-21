package com.dtu.ddd.ecommerce.sales.order.infrastructure;

import com.dtu.ddd.ecommerce.sales.order.domain.OrderItem;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.Quantity;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.util.UUID;

@NoArgsConstructor
class OrderItemDatabaseEntity {
    @Setter UUID product_id;
    @Setter Double money;
    @Setter String currency;
    @Setter Integer quantity;

    OrderItem toDomainModel() {
        return new OrderItem(
                new ProductId(product_id),
                Money.of(CurrencyUnit.of(currency), money),
                new Quantity(quantity)
        );
    }
}
