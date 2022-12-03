package com.dtu.ddd.ecommerce.web;

import com.dtu.ddd.ecommerce.sales.order.application.OrderApplicationService;
import com.dtu.ddd.ecommerce.sales.order.application.commands.SubmitOrderCommand;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
@SuppressWarnings("rawtypes")
public class OrderController {
  private final OrderApplicationService orderApplicationService;

  @PostMapping("/submit")
  public ResponseEntity add(@RequestBody SubmitOrderRequest request) {
    orderApplicationService.submitOrder(request.toCommand());
    return ResponseEntity.ok().build();
  }

  public record SubmitOrderRequest(UUID cartId, String street, String houseNumber, String city, String zipCode) {
    private SubmitOrderCommand toCommand() {
      return new SubmitOrderCommand(cartId, street, houseNumber, city, zipCode);
    }
  }
}
