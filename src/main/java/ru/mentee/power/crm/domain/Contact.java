package ru.mentee.power.crm.domain;

public record Contact (String email, String phone, Address address) {
  public Contact {
    if (email == null || email.isEmpty()) {
      throw new IllegalArgumentException("E-mail can`t be empty");
    }

    if (phone == null || phone.isEmpty()) {
      throw new IllegalArgumentException("Phone can`t be empty");
    }

    if (address == null) {
      throw new IllegalArgumentException("Address can`t be empty");
    }

  }
}
