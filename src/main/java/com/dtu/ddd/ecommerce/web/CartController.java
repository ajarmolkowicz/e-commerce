package com.dtu.ddd.ecommerce.web;

import com.dtu.ddd.ecommerce.sales.cart.application.CartApplicationService;
import com.dtu.ddd.ecommerce.sales.cart.application.command.AddProductToCartCommand;
import com.dtu.ddd.ecommerce.sales.cart.application.command.DeleteProductFromCartCommand;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jmolecules.architecture.hexagonal.PrimaryAdapter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PrimaryAdapter
@RestController
@RequestMapping("/cart/item")
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class CartController {
  private final CartApplicationService cartApplicationService;

  @PostMapping("/add")
  public ResponseEntity add(@RequestBody AddProductToCartRequest request) {
    cartApplicationService.addProductToCart(request.toCommand());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/delete")
  public ResponseEntity delete(@RequestBody DeleteProductFromCartRequest request) {
    cartApplicationService.deleteProductFromCart(request.toCommand());
    return ResponseEntity.ok().build();
  }

  public record AddProductToCartRequest(UUID cartId, UUID productId, Integer quantity) {
    private AddProductToCartCommand toCommand() {
      return new AddProductToCartCommand(cartId, productId, quantity);
    }
  }

  public record DeleteProductFromCartRequest(UUID cartId, UUID productId) {
    private DeleteProductFromCartCommand toCommand() {
      return new DeleteProductFromCartCommand(cartId, productId);
    }
  }
}
