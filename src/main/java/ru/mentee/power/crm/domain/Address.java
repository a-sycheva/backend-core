package ru.mentee.power.crm.domain;

public record Address(String city, String street, String zip) {
  public Address {
    if (city == null || city.isEmpty()) {
      throw new IllegalArgumentException("City can`t be empty");
    }

    if (zip == null || zip.isEmpty()) {
      throw new IllegalArgumentException("Zip can`t be empty");
    }

  }
}
