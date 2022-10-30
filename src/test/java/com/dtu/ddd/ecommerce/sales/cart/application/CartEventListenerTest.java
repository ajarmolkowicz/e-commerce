package com.dtu.ddd.ecommerce.sales.cart.application;

import com.dtu.ddd.ecommerce.sales.cart.domain.Cart;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartId;
import com.dtu.ddd.ecommerce.sales.cart.domain.CartRepository;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderEvents;
import com.dtu.ddd.ecommerce.sales.order.domain.OrderId;
import com.dtu.ddd.ecommerce.shared.vo.Address;
import java.util.Optional;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.dtu.ddd.ecommerce.utils.Assertions.assertCaptureSatisfies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartEventListenerTest {
  @Mock private CartRepository cartRepository;

  private CartEventListener listener;

  @BeforeEach
  void setUp() {
    listener = new CartEventListener(cartRepository);
  }

  @Test
  void orderSubmitted() {
    //GIVEN
    final var cartId = CartId.generate();
    final var cart = new Cart(cartId);
    when(cartRepository.find(cartId)).thenReturn(Optional.of(cart));

    //WHEN
    listener.handle(new OrderEvents.OrderSubmitted(OrderId.generate(), cartId, Money.parse("EUR 100"), new Address(
        new Address.Street("Amager Strandvej 1"),
        new Address.HouseNumber("st. th."),
        new Address.City("Copenhagen"),
        new Address.ZipCode("2300")
    )));

    //THEN
    assertCaptureSatisfies($ -> verify(cartRepository).save($.capture()), captured -> {
      assertThat(captured.getId()).isEqualTo(cartId);
      assertThat(captured.getItems()).isEmpty();
    }, Cart.class);
  }
}