package com.dtu.ddd.ecommerce.sales.product.domain;

import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import java.util.Optional;
import java.util.Set;
import org.jmolecules.architecture.hexagonal.SecondaryPort;

import static java.lang.String.format;

@SecondaryPort
public interface ProductRepository {
  void save(Product product);

  void delete(Product product);

  Optional<Product> find(ProductId id);

  Set<Product> find(Set<ProductId> ids);

  interface Exceptions {
    class ProductNotFound extends BusinessException {
      public ProductNotFound(ProductId id) {
        super(format("Product with id: %s not found", id.id().toString()));
      }
    }
  }
}
