package com.dtu.ddd.ecommerce.portal;

import com.dtu.ddd.ecommerce.web.CartController;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
class CartActions {
  private final TestRestTemplate template;
  private final Integer serverPort;

  public ResponseEntity addProductToCart(UUID cartId, UUID productId, Integer quantity) {
    return template.exchange("http://localhost:" + serverPort + "/cart/item/add", HttpMethod.POST,
        new HttpEntity<>(new CartController.AddProductToCartRequest(cartId, productId, quantity)), Object.class); // TODO : Response type
  }
}
