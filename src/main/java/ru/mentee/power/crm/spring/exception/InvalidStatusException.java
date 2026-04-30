package ru.mentee.power.crm.spring.exception;

public class InvalidStatusException extends RuntimeException {

  public InvalidStatusException(String status) {
    super("Invalid status: " + status + ". Must be ACTIVE or INACTIVE");
  }
}
