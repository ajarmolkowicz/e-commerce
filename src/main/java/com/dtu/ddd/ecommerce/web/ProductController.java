package com.dtu.ddd.ecommerce.web;

import com.dtu.ddd.ecommerce.sales.product.application.ProductApplicationService;
import com.dtu.ddd.ecommerce.sales.product.application.command.AddProductCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.DeleteProductCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductDescriptionCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductPriceCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductQuantityCommand;
import com.dtu.ddd.ecommerce.sales.product.application.command.EditProductTitleCommand;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class ProductController {
  private final ProductApplicationService productService;

  @PostMapping
  public ResponseEntity<UUID> product(@RequestBody AddProductRequest request) {
    final var id = productService.addProduct(request.toCommand());
    return ResponseEntity.ok().body(id.id());
  }

  @PostMapping("/title/{id}")
  public ResponseEntity title(@PathVariable UUID id, @RequestBody EditProductTitleRequest request) {
    productService.editProductTitle(request.toCommand(id));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/description/{id}")
  public ResponseEntity description(@PathVariable UUID id, @RequestBody EditProductDescriptionRequest request) {
    productService.editProductDescription(request.toCommand(id));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/price/{id}")
  public ResponseEntity price(@PathVariable UUID id, @RequestBody EditProductPriceRequest request) {
    productService.editProductPrice(request.toCommand(id));
    return ResponseEntity.ok().build();
  }

  @PostMapping("/quantity/{id}")
  public ResponseEntity quantity(@PathVariable UUID id, @RequestBody EditProductQuantityRequest request) {
    productService.editProductQuantity(request.toCommand(id));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity product(@PathVariable UUID id) {
    productService.deleteProduct(new DeleteProductCommand(id));
    return ResponseEntity.ok().build();
  }

  public record AddProductRequest(String title, String description, Double price, String currency, Integer quantity) {
    private AddProductCommand toCommand() {
      return new AddProductCommand(title, description, price, currency, quantity);
    }
  }

  public record EditProductTitleRequest(String title) {
    private EditProductTitleCommand toCommand(UUID id) {
      return new EditProductTitleCommand(id, title);
    }
  }

  public record EditProductDescriptionRequest(String description) {
    private EditProductDescriptionCommand toCommand(UUID id) {
      return new EditProductDescriptionCommand(id, description);
    }
  }

  public record EditProductPriceRequest(Double price, String currency) {
    private EditProductPriceCommand toCommand(UUID id) {
      return new EditProductPriceCommand(id, price, currency);
    }
  }

  public record EditProductQuantityRequest(Integer quantity) {
    private EditProductQuantityCommand toCommand(UUID id) {
      return new EditProductQuantityCommand(id, quantity);
    }
  }
}
