package com.dtu.ddd.ecommerce.shared.aggregates;

public record Version(Integer version) {
  public static Version zero() {
    return new Version(0);
  }

  public static Version of(Integer version) {
    return new Version(version);
  }
}
