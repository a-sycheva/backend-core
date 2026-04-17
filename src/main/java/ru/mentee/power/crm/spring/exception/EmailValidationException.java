package ru.mentee.power.crm.spring.exception;

import org.springframework.http.HttpStatusCode;

public class EmailValidationException extends RuntimeException {
  private final HttpStatusCode statusCode;

  public EmailValidationException(String message, HttpStatusCode statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

}
