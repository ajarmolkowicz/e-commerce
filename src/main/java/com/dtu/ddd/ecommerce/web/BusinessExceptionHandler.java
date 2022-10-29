package com.dtu.ddd.ecommerce.web;

import com.dtu.ddd.ecommerce.shared.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.valid4j.Assertive.require;

@RestControllerAdvice
public class BusinessExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<Message> handle(BusinessException e) {
    return ResponseEntity.badRequest().body(Message.from(e));
  }

  private record Message(String message){
    private static Message from(BusinessException e) {
      final String message = e.getMessage();
      require(message != null, "Error message cannot be null");
      require(!message.isEmpty(), "Error message cannot be empty");
      return new Message(message);
    }
  }
}
