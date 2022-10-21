package com.dtu.ddd.ecommerce.billing.infrastructure;

import com.dtu.ddd.ecommerce.billing.domain.CollectionResult;
import com.dtu.ddd.ecommerce.billing.domain.Payment;
import com.dtu.ddd.ecommerce.billing.domain.PaymentId;
import com.dtu.ddd.ecommerce.billing.domain.PaymentRepository;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.of;
import static java.lang.String.format;

@RequiredArgsConstructor
public class PaymentDatabaseRepository implements PaymentRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override public Optional<Payment> find(PaymentId id) {
        final var o = Try.ofSupplier(
                        () -> of(jdbcTemplate.queryForObject("SELECT p.* FROM payments p WHERE p.payment_id = ?",
                                new BeanPropertyRowMapper<>(PaymentDatabaseEntity.class), id.id())))
                .getOrElse(none());
        return o.map($ -> Optional.of($.toDomainModel())).getOrElse(Optional.empty());
    }

    @Override public void save(Payment payment) {
        find(payment.getId())
                .ifPresentOrElse(
                        entity -> update(payment),
                        () -> insertNew(payment)
                );
    }
    private void insertNew(Payment payment) {
        jdbcTemplate.update(
                "INSERT INTO payments VALUES (?, ?, ?, ?, ?, ?, ?)",
                payment.getId().id(),
                payment.getReferenceId().id(),
                payment.getTotal().getAmount().doubleValue(),
                payment.getTotal().getCurrencyUnit().toString(),
                payment.getRequestTime().time(),
                Optional.ofNullable(payment.getCollectionResult()).map(CollectionResult::collected).orElse(null),
                0);
    }

    private void update(Payment payment) {
        final var result = jdbcTemplate.update("UPDATE payments SET " +
                        "total = ?, " +
                        "currency = ?, " +
                        "request_time = ?, " +
                        "collection_result = ?, " +
                        "version = ? " +
                        "WHERE payment_id = ?" +
                        "AND version = ?",
                payment.getTotal().getAmount().doubleValue(),
                payment.getTotal().getCurrencyUnit().toString(),
                payment.getRequestTime().time(),
                payment.getCollectionResult().collected(),
                payment.getVersion().version() + 1,
                payment.getId().id(),
                payment.getVersion().version());
        if (result == 0) {
            throw new Exceptions.PaymentIsStaleException(payment.getId());
        }
    }

    interface Exceptions {
        class PaymentIsStaleException extends RuntimeException {
            PaymentIsStaleException(PaymentId id) {
                super(format("Payment: %s aggregate root is stale", id.id()));
            }
        }
    }
}
