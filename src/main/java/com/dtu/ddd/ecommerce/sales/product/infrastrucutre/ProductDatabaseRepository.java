package com.dtu.ddd.ecommerce.sales.product.infrastrucutre;

import com.dtu.ddd.ecommerce.sales.product.domain.Product;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductId;
import com.dtu.ddd.ecommerce.sales.product.domain.ProductRepository;
import io.vavr.control.Try;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import static io.vavr.control.Option.of;
import static io.vavr.control.Option.none;
import static java.lang.String.format;

@RequiredArgsConstructor
public class ProductDatabaseRepository implements ProductRepository {
  private final JdbcTemplate jdbcTemplate;

  @Override public void save(Product product) {
    find(product.getId())
        .map(entity -> update(product))
        .orElseGet(() -> insertNew(product));
  }

  @Override public void delete(Product product) {
    jdbcTemplate.update("UPDATE products SET " +
            "status = ?, " +
            "version = ? " +
            "WHERE id = ? " +
            "AND version = ?",
        "DELETED",
        product.getVersion().version() + 1,
        product.getId().id(),
        product.getVersion().version());
  }

  @Override public Optional<Product> find(ProductId id) {
    final var p = Try.ofSupplier(
            () -> of(jdbcTemplate.queryForObject("SELECT p.* FROM products p WHERE p.id = ?",
                new BeanPropertyRowMapper<>(ProductDatabaseEntity.class), id.id())))
        .getOrElse(none());
    return p.filter(ProductDatabaseEntity::isUsable)
        .map($ -> Optional.of($.toDomainModel()))
        .getOrElse(Optional.empty());
  }

  @Override public Set<Product> find(Set<ProductId> ids) {
    return null;
  }

  private int insertNew(Product product) {
    return jdbcTemplate.update(
        "INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?)",
        product.getId().id(),
        product.getTitle().title(),
        product.getDescription().description(),
        product.getPrice().toString(),
        product.getQuantity().value(),
        "USABLE",
        0);
  }

  private int update(Product product) {
    final var result = jdbcTemplate.update("UPDATE products SET " +
            "title = ?, " +
            "description = ?, " +
            "price = ?, " +
            "quantity = ?, " +
            "version = ? " +
            "WHERE id = ? " +
            "AND version = ?",
        product.getTitle().title(),
        product.getDescription().description(),
        product.getPrice().toString(),
        product.getQuantity().value(),
        product.getVersion().version() + 1,
        product.getId().id(),
        product.getVersion().version()
    );
    if (result == 0) {
      throw new Exceptions.ProductIsStaleException(product.getId());
    }
    return result;
  }

  interface Exceptions {
    class ProductIsStaleException extends RuntimeException {
      ProductIsStaleException(ProductId id) {
        super(format("Product: %s aggregate root is stale", id.id()));
      }
    }
  }
}
