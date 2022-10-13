package com.dtu.ddd.ecommerce.sales.product.domain;

import java.util.List;

public interface ProductsReadModel {
  List<Product> findAvailableProducts();

  void handle(ProductEvents.ProductAdded event);
}
