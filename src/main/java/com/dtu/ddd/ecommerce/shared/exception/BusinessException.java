package com.dtu.ddd.ecommerce.shared.exception;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }
}
