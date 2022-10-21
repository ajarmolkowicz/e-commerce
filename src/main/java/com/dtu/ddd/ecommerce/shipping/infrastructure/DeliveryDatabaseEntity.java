package com.dtu.ddd.ecommerce.shipping.infrastructure;

import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.dtu.ddd.ecommerce.shipping.domain.Address;
import com.dtu.ddd.ecommerce.shipping.domain.Delivery;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryId;
import com.dtu.ddd.ecommerce.shipping.domain.OrderId;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
class DeliveryDatabaseEntity {
    @Setter UUID delivery_id;
    @Setter UUID order_id;
    @Setter String street;
    @Setter String house_number;
    @Setter String city;
    @Setter String zip_code;
    @Setter String shipping_state;
    @Setter Integer version;

    Delivery toDomainModel() {
        return new Delivery(
            new DeliveryId(delivery_id),
                new OrderId(order_id),
                new Address(
                        new Address.Street(street),
                        new Address.HouseNumber(house_number),
                        new Address.City(city),
                        new Address.ZipCode(zip_code)),
                Delivery.State.valueOf(shipping_state),
                new Version(version)
        );
    }
}
