package com.dtu.ddd.ecommerce.shipping.application;

import com.dtu.ddd.ecommerce.billing.domain.PaymentEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shipping.domain.Delivery;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryRepository;
import com.dtu.ddd.ecommerce.shipping.domain.OrderId;
import com.dtu.ddd.ecommerce.shipping.domain.ShippingEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

@RequiredArgsConstructor
public class ShippingEventsHandler {
  private final DeliveryRepository deliveryRepository;
  private final DomainEventPublisher eventPublisher;

  @EventListener @Order(1)
  public void handle(OrderEvents.OrderSubmitted orderSubmitted) {
    final var delivery = new Delivery(OrderId.of(orderSubmitted.getOrderId().id()), orderSubmitted.getAddress());
    deliveryRepository.save(delivery);
  }

  @EventListener
  public void handle(PaymentEvents.PaymentCollected paymentCollected) {
    final var orderId = OrderId.of(paymentCollected.getReferenceId().id());
    final var delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> new DeliveryRepository.Exceptions.DeliveryNotFound(orderId));
    delivery.dispatch();
    deliveryRepository.save(delivery);
    eventPublisher.publish(new ShippingEvents.OrderShipped(delivery.getOrderId()));
  }

  @EventListener
  public void handle(PaymentEvents.PaymentCollectionFailed paymentCollectionFailed) {
    final var orderId = OrderId.of(paymentCollectionFailed.getReferenceId().id());
    final var delivery = deliveryRepository.findByOrderId(orderId).orElseThrow(() -> new DeliveryRepository.Exceptions.DeliveryNotFound(orderId));
    deliveryRepository.delete(delivery.getId());
  }
}
