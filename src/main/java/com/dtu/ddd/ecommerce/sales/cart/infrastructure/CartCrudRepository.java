package com.dtu.ddd.ecommerce.sales.cart.infrastructure;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface CartCrudRepository extends CrudRepository<CartDatabaseEntity, UUID> {
}
