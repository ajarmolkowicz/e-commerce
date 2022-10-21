package com.dtu.ddd.ecommerce.shipping.infrastructure;

import com.dtu.ddd.ecommerce.shipping.domain.Delivery;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryId;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static java.lang.String.format;

@RequiredArgsConstructor
public class DeliveryDatabaseRepository implements DeliveryRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Delivery> find(DeliveryId id) {
        final var o = Try.ofSupplier(
                        () -> of(jdbcTemplate.queryForObject("SELECT d.* FROM deliveries d WHERE d.delivery_id = ?",
                                new BeanPropertyRowMapper<>(DeliveryDatabaseEntity.class), id.id())))
                .getOrElse(none());
        return o.map($ -> Optional.of($.toDomainModel())).getOrElse(Optional.empty());
    }

    @Override
    public void save(Delivery delivery) {
        find(delivery.getId())
                .ifPresentOrElse(
                        entity -> update(delivery),
                        () -> insertNew(delivery)
                );
    }

    private void insertNew(Delivery delivery) {
        jdbcTemplate.update(
                "INSERT INTO orders VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                delivery.getId().id(),
                delivery.getOrderId().id(),
                delivery.getAddress().getStreet(),
                delivery.getAddress().getHouseNumber(),
                delivery.getAddress().getCity(),
                delivery.getAddress().getZipCode(),
                delivery.getState().toString(),
                0);
    }

    private void update(Delivery delivery) {
        final var result = jdbcTemplate.update("UPDATE orders SET " +
                        "street = ?, " +
                        "house_number = ?, " +
                        "city = ?, " +
                        "zip_code = ?, " +
                        "shipping_state = ?, " +
                        "version = ?" +
                        "WHERE delivery_id = ?" +
                        "AND version = ?",
                delivery.getAddress().getStreet().street(),
                delivery.getAddress().getHouseNumber().number(),
                delivery.getAddress().getCity().city(),
                delivery.getAddress().getZipCode().zipCode(),
                delivery.getState().toString(),
                delivery.getVersion().version() + 1,
                delivery.getId().id(),
                delivery.getVersion().version());

        if (result == 0) {
            throw new Exceptions.DeliveryIsStaleException(delivery.getId());
        }
    }

    interface Exceptions {
        class DeliveryIsStaleException extends RuntimeException {
            DeliveryIsStaleException(DeliveryId id) {
                super(format("Delivery: %s aggregate root is stale", id.id()));
            }
        }
    }
}
