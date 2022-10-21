package com.dtu.ddd.ecommerce.billing.application;

import com.dtu.ddd.ecommerce.billing.domain.*;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@RequiredArgsConstructor
public class PaymentEventsHandler {
    private final PaymentRepository paymentRepository;
    private final DomainEventPublisher eventPublisher;
    private final PaymentProvider paymentProvider;

    @EventListener
    public void handle(OrderEvents.OrderSubmitted orderSubmitted) {
        final var payment = new Payment(new ReferenceId(orderSubmitted.getOrderId().id()), orderSubmitted.getTotal());
        final var result = new CollectionResult(paymentProvider.collect(payment.getReferenceId()));
        payment.onCollectionFinished(result);

        paymentRepository.save(payment);

        eventPublisher.publish(
                API.Match(result).of(
                        Case($(CollectionResult::collected), () -> new PaymentEvents.PaymentCollected(payment.getReferenceId())),
                        Case($(), () -> new PaymentEvents.PaymentCollectionFailed(payment.getReferenceId())))
        );
    }
}
