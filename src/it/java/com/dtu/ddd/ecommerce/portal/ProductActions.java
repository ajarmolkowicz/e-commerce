package com.dtu.ddd.ecommerce.portal;

import com.dtu.ddd.ecommerce.web.ProductController;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
class ProductActions {
  private final TestRestTemplate template;
  private final Integer serverPort;

  public ResponseEntity<UUID> add(String title, String description, Double price, String currency, Integer quantity) {
    return template.exchange("http://localhost:" + serverPort + "/products", HttpMethod.POST,
        new HttpEntity<>(new ProductController.AddProductRequest(title, description, price, currency,quantity)), UUID.class);
  }

  public ResponseEntity editTitle(UUID id, String title) {
    return template.exchange("http://localhost:" + serverPort + "/products/title/" + id,
        HttpMethod.POST, new HttpEntity<>(new ProductController.EditProductTitleRequest(title)), Object.class);
  }

  public ResponseEntity editDescription(UUID id, String description) {
    return template.exchange("http://localhost:" + serverPort + "/products/description/" + id,
        HttpMethod.POST, new HttpEntity<>(new ProductController.EditProductDescriptionRequest(description)), Object.class);
  }

  public ResponseEntity editPrice(UUID id, Double price, String currency) {
    return template.exchange("http://localhost:" + serverPort + "/products/price/" + id,
        HttpMethod.POST, new HttpEntity<>(new ProductController.EditProductPriceRequest(price, currency)), Object.class);
  }

  public ResponseEntity editQuantity(UUID id, Integer quantity) {
    return template.exchange("http://localhost:" + serverPort + "/products/quantity/" + id,
        HttpMethod.POST, new HttpEntity<>(new ProductController.EditProductQuantityRequest(quantity)), Object.class);
  }

  public ResponseEntity delete(UUID id) {
    return template.exchange("http://localhost:" + serverPort + "/products/" + id, HttpMethod.DELETE, HttpEntity.EMPTY, Object.class);
  }
}
