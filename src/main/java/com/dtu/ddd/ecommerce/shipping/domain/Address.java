package com.dtu.ddd.ecommerce.shipping.domain;

import lombok.Value;
import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
@Value public class Address {
  Street street;
  HouseNumber houseNumber;
  City city;
  ZipCode zipCode;

  public Address(Street street, HouseNumber houseNumber, City city, ZipCode zipCode) {
    this.street = street;
    this.houseNumber = houseNumber;
    this.city = city;
    this.zipCode = zipCode;
  }

  public record Street(String street) {
  }

  public record HouseNumber(String number) {
  }

  public record City(String city) {
  }

  public record ZipCode(String zipCode) {
  }
}
