package com.dtu.ddd.ecommerce.sales.cart.domain;

import java.util.Set;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Entity;

@AggregateRoot @Entity
public class Cart {
  private CartId id;
  private Set<CartItem> items;

  boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  void add() {
    throw new UnsupportedOperationException();
  }

  void remove() {
    throw new UnsupportedOperationException();
  }
}
