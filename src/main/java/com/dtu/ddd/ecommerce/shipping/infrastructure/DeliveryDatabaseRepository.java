package com.dtu.ddd.ecommerce.shipping.infrastructure;

import com.dtu.ddd.ecommerce.shipping.domain.Delivery;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryId;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryRepository;
import com.dtu.ddd.ecommerce.shipping.domain.OrderId;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    public Optional<Delivery> findByOrderId(OrderId id) {
        final var o = Try.ofSupplier(
                        () -> of(jdbcTemplate.queryForObject("SELECT d.* FROM deliveries d WHERE d.order_id = ?",
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

    @Override @SneakyThrows
    public void delete(DeliveryId id) {
        final var result = jdbcTemplate.update("DELETE FROM deliveries WHERE delivery_id = ?", id.id());
        if (result == 0) {
            throw new Exceptions.DeliveryDeleteException(id);
        }
    }

    private void insertNew(Delivery delivery) {
        jdbcTemplate.update(
                "INSERT INTO deliveries VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                delivery.getId().id(),
                delivery.getOrderId().id(),
                delivery.getAddress().street().name(),
                delivery.getAddress().houseNumber().number(),
                delivery.getAddress().city().name(),
                delivery.getAddress().zipCode().code(),
                delivery.getState().toString(),
                0);
    }

    private void update(Delivery delivery) {
        final var result = jdbcTemplate.update("UPDATE deliveries SET " +
                        "street = ?, " +
                        "house_number = ?, " +
                        "city = ?, " +
                        "zip_code = ?, " +
                        "shipping_state = ?, " +
                        "version = ?" +
                        "WHERE delivery_id = ?" +
                        "AND version = ?",
                delivery.getAddress().street().name(),
                delivery.getAddress().houseNumber().number(),
                delivery.getAddress().city().name(),
                delivery.getAddress().zipCode().code(),
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

        class DeliveryDeleteException extends Throwable {
            DeliveryDeleteException(DeliveryId id) {
                super(format("Delivery: %s could not be deleted", id.id()));
            }
        }
    }
}
