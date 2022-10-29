package com.dtu.ddd.ecommerce.shipping.infrastructure;

import com.dtu.ddd.ecommerce.shared.aggregates.Version;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import com.dtu.ddd.ecommerce.shipping.ShippingTestContext;
import com.dtu.ddd.ecommerce.shipping.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShippingTestContext.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DeliveryDatabaseRepositoryIT {
  @Autowired
  private DeliveryRepository deliveryRepository;

  @Test
  void crud() {
    //WHEN
    assertThat(deliveryRepository.find(DeliveryId.generate())).isEmpty();

    //GIVEN
    final var orderId = OrderId.generate();
    final var address = new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    );
    final var delivery = new Delivery(orderId, address);

    //WHEN
    deliveryRepository.save(delivery);

    //THEN
    final var saved = deliveryRepository.find(delivery.getId());
    assertThat(saved).isNotEmpty();
    assertThat(saved.get()).satisfies(
        $ -> assertThat($.getOrderId()).isEqualTo(orderId),
        $ -> assertThat($.getAddress()).isEqualTo(address),
        $ -> assertThat($.getState()).isEqualTo(Delivery.State.REGISTERED),
        $ -> assertThat($.getVersion()).isEqualTo(Version.zero())
    );

    //WHEN
    saved.get().dispatch();
    deliveryRepository.save(saved.get());

    //THEN
    final var updated = deliveryRepository.find(delivery.getId());
    assertThat(updated).isNotEmpty();
    assertThat(updated.get()).satisfies(
        $ -> assertThat($.getState()).isEqualTo(Delivery.State.IN_DELIVERY),
        $ -> assertThat($.getVersion()).isEqualTo(Version.of(1))
    );

    //WHEN
    deliveryRepository.delete(delivery.getId());

    //THEN
    final var deleted = deliveryRepository.find(delivery.getId());
    assertThat(deleted).isEmpty();
  }

  @Test
  void findByOrderId() {
    //GIVEN
    final var orderId = OrderId.generate();
    final var address = new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    );
    final var delivery = new Delivery(orderId, address);
    assertThat(deliveryRepository.find(DeliveryId.generate())).isEmpty();

    //WHEN
    deliveryRepository.save(delivery);

    //THEN
    final var byOrderId = deliveryRepository.findByOrderId(orderId);
    assertThat(byOrderId).isNotEmpty();
    assertThat(byOrderId.get()).satisfies(
        $ -> assertThat($.getId()).isEqualTo(delivery.getId()),
        $ -> assertThat($.getOrderId()).isEqualTo(delivery.getOrderId()),
        $ -> assertThat($.getAddress()).isEqualTo(delivery.getAddress()),
        $ -> assertThat($.getState()).isEqualTo(delivery.getState())
    );
  }
}