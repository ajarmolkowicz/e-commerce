package com.dtu.ddd.ecommerce.sales.order.application.commands;

import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import lombok.Getter;

import java.util.UUID;

public class SubmitOrderCommand {
  @Getter private final CartId cartId;
  @Getter private final Address address;

  public SubmitOrderCommand(UUID cartId, String street, String houseNumber, String city, String zipCode) {
    this.cartId = new CartId(cartId);
    this.address = new Address(new Address.Street(street), new Address.HouseNumber(houseNumber), new Address.City(city), new Address.ZipCode(zipCode));
  }
}

