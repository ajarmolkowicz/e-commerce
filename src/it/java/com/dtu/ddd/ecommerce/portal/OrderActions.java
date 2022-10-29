package com.dtu.ddd.ecommerce.portal;

import com.dtu.ddd.ecommerce.web.OrderController;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
class OrderActions {
  private final TestRestTemplate template;
  private final Integer serverPort;

  public ResponseEntity submitOrder(UUID cartId, String street, String houseNumber, String city, String zipCode) {
    return template.exchange("http://localhost:" + serverPort + "/orders/submit", HttpMethod.POST,
        new HttpEntity<>(new OrderController.SubmitOrderRequest(cartId, street, houseNumber, city, zipCode)), Object.class);
  }
}
