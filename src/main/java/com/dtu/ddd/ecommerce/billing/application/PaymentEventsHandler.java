package com.dtu.ddd.ecommerce.billing.application;

import com.dtu.ddd.ecommerce.billing.domain.*;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import io.vavr.API;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static java.lang.String.format;

@RequiredArgsConstructor
public class PaymentEventsHandler {
  private final PaymentRepository paymentRepository;
  private final DomainEventPublisher eventPublisher;
  private final PaymentProvider paymentProvider;

  @EventListener @Order(2)
  public void handle(OrderEvents.OrderSubmitted orderSubmitted) {
    final var payment = new Payment(new ReferenceId(orderSubmitted.getOrderId().id()), orderSubmitted.getTotal());
    final var result = new CollectionResult(paymentProvider.collect(payment.getReferenceId()));
    payment.onCollectionFinished(result);

    paymentRepository.save(payment);

    if(result.collected()) {
      eventPublisher.publish(new PaymentEvents.PaymentCollected(payment.getReferenceId()));
    } else {
      eventPublisher.publish(new PaymentEvents.PaymentCollectionFailed(payment.getReferenceId()));
      throw new Exceptions.PaymentCollectionFailed(payment.getReferenceId());
    }
  }

  public interface Exceptions {
    class PaymentCollectionFailed extends BusinessException {
      public PaymentCollectionFailed(ReferenceId id) {
        super(format("Payment for order with id: %s failed", id.id().toString()));
      }
    }
  }
}
