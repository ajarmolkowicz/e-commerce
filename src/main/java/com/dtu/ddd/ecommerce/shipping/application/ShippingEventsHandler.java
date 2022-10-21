package com.dtu.ddd.ecommerce.shipping.application;

import com.dtu.ddd.ecommerce.billing.domain.PaymentEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class ShippingEventsHandler {
    private final DeliveryRepository deliveryRepository;
    private final DomainEventPublisher eventPublisher;

    @EventListener
    private void handle(OrderEvents.OrderSubmitted orderSubmitted) {

    }

    @EventListener
    private void handle(PaymentEvents.PaymentCollected paymentCollected) {

    }

    @EventListener
    private void handle(PaymentEvents.PaymentCollectionFailed paymentCollectionFailed) {

    }
}
