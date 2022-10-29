package com.dtu.ddd.ecommerce.shipping.application;

import com.dtu.ddd.ecommerce.billing.domain.PaymentEvents;
import com.dtu.ddd.ecommerce.billing.domain.ReferenceId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.shared.event.DomainEventPublisher;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import com.dtu.ddd.ecommerce.shipping.domain.Delivery;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryId;
import com.dtu.ddd.ecommerce.shipping.domain.DeliveryRepository;
import com.dtu.ddd.ecommerce.shipping.domain.OrderId;
import java.util.Optional;
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
class ShippingEventsHandlerTest {
  @Mock private DeliveryRepository deliveryRepository;
  @Mock private DomainEventPublisher eventPublisher;

  private ShippingEventsHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ShippingEventsHandler(deliveryRepository, eventPublisher);
  }

  @DisplayName("Order submitted, should new delivery in INITIALIZED state be persisted")
  @Test
  void orderSubmitted() {
    //GIVEN
    final var orderId = OrderId.generate();
    final var money = Money.parse("EUR 150");
    final var address = new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    );

    //WHEN
    handler.handle(new OrderEvents.OrderSubmitted(
        com.dtu.ddd.ecommerce.sales.order.domain.OrderId.of(orderId.id()), CartId.generate(), money, address));

    //THEN
    assertCaptureSatisfies($ -> verify(deliveryRepository).save($.capture()), delivery -> {
      assertThat(delivery.getOrderId()).isEqualTo(orderId);
      assertThat(delivery.getAddress()).isEqualTo(address);
      assertThat(delivery.getState()).isEqualTo(Delivery.State.REGISTERED);
    }, Delivery.class);
  }

  @DisplayName("Payment collected, should dispatch a delivery")
  @Test
  void paymentCollected() {
    //GIVEN
    final var orderId = OrderId.generate();
    final var delivery = new Delivery(orderId, new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    ));
    when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

    //WHEN
    handler.handle(new PaymentEvents.PaymentCollected(ReferenceId.of(orderId.id())));

    //THEN
    assertCaptureSatisfies($ -> verify(deliveryRepository).save($.capture()), captured -> {
      assertThat(captured.getId()).isEqualTo(delivery.getId());
      assertThat(captured.getState()).isEqualTo(Delivery.State.IN_DELIVERY);
    }, Delivery.class);
  }

  @Test
  void paymentCollectionFailed() {
    //GIVEN
    final var orderId = OrderId.generate();
    final var delivery = new Delivery(orderId, new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    ));
    when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

    //WHEN
    handler.handle(new PaymentEvents.PaymentCollectionFailed(ReferenceId.of(orderId.id())));

    //THEN
    assertCaptureSatisfies($ -> verify(deliveryRepository).delete($.capture()), id -> {
      assertThat(id).isEqualTo(delivery.getId());
    }, DeliveryId.class);
  }
}