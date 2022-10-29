package com.dtu.ddd.ecommerce.billing.application;

import com.dtu.ddd.ecommerce.billing.domain.*;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.dtu.ddd.ecommerce.utils.Assertions.assertCaptureSatisfies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentEventsHandlerTest {
  @Mock private PaymentProvider paymentProvider;
  @Mock private PaymentRepository paymentRepository;
  @Mock private DomainEventPublisher eventPublisher;

  private PaymentEventsHandler handler;

  @BeforeEach
  void setUp() {
    handler = new PaymentEventsHandler(paymentRepository, eventPublisher, paymentProvider);
  }

  @DisplayName("Payment collected, should positive result be persisted and success event be published")
  @Test
  void paymentCollected() {
    //GIVEN
    final var orderId = OrderId.generate();
    final var money = Money.parse("EUR 150");
    final var address = new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    );
    when(paymentProvider.collect(new ReferenceId(orderId.id()))).thenReturn(true);

    //WHEN
    handler.handle(new OrderEvents.OrderSubmitted(orderId, CartId.generate(), money, address));

    //THEN
    assertCaptureSatisfies($ -> verify(paymentRepository).save($.capture()), payment -> {
      assertThat(payment.getReferenceId()).isEqualTo(new ReferenceId(orderId.id()));
      assertThat(payment.getCollectionResult().collected()).isEqualTo(true);
    }, Payment.class);

    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getReferenceId()).isEqualTo(new ReferenceId(orderId.id())), PaymentEvents.PaymentCollected.class);
  }

  @DisplayName("Payment collection failed, should negative result be persisted and failure event be published")
  @Test
  void paymentCollectionFailed() {
    //GIVEN
    final var orderId = OrderId.generate();
    final var address = new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    );
    when(paymentProvider.collect(new ReferenceId(orderId.id()))).thenReturn(false);

    //WHEN
    handler.handle(new OrderEvents.OrderSubmitted(orderId, CartId.generate(), Money.parse("EUR 150"), address));

    //THEN
    assertCaptureSatisfies($ -> verify(paymentRepository).save($.capture()), payment -> {
      assertThat(payment.getReferenceId()).isEqualTo(new ReferenceId(orderId.id()));
      assertThat(payment.getTotal()).isEqualTo(Money.parse("EUR 150"));
      assertThat(payment.getCollectionResult().collected()).isEqualTo(false);
    }, Payment.class);

    assertCaptureSatisfies($ -> verify(eventPublisher).publish($.capture()),
        event -> assertThat(event.getReferenceId()).isEqualTo(new ReferenceId(orderId.id())), PaymentEvents.PaymentCollectionFailed.class);
  }
}