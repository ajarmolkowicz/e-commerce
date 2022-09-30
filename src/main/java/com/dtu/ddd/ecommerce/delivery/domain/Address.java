package com.dtu.ddd.ecommerce.delivery.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public class Address {
  private Street street;
  private HouseNumber houseNumber;
  private City city;
  private ZipCode zipCode;

  static class Street {
    // TODO
  }

  static class HouseNumber {
    // TODO
  }

  static class City {
    // TODO
  }

  static class ZipCode {
    // TODO
  }
}
