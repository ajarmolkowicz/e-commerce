package com.dtu.ddd.ecommerce.sales.order.application.commands;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import lombok.Getter;

import java.util.UUID;

public class SubmitOrderCommand {
    @Getter private final CartId cartId;
    @Getter private final Address address;

    public SubmitOrderCommand(UUID cartId, Address.Street street, Address.HouseNumber houseNumber, Address.City city, Address.ZipCode zipCode) {
        this.cartId = new CartId(cartId);
        this.address = new Address(street, houseNumber, city, zipCode);
    }
}

