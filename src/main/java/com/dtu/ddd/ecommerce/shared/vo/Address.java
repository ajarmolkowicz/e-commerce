package com.dtu.ddd.ecommerce.shared.vo;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record Address(Address.Street street, Address.HouseNumber houseNumber, Address.City city, Address.ZipCode zipCode) {
  public record Street(String name) {
  }

  public record HouseNumber(String number) {
  }

  public record City(String name) {
  }

  public record ZipCode(String code) {
  }
}
