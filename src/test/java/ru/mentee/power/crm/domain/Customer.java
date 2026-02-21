package ru.mentee.power.crm.domain;

import java.util.Set;
import java.util.UUID;

public record Customer(UUID id,
                       Contact contact,
                       Address billingAddress,
                       String loyaltyTier) {

  public Customer {
    if (id == null) {
      throw new IllegalArgumentException("Id can`t be empty");
    }

    if (contact == null) {
      throw new IllegalArgumentException("Contact can`t be empty");
    }

    if (billingAddress == null) {
      throw new IllegalArgumentException("Billing address can`t be empty");
    }

    if (loyaltyTier == null || loyaltyTier.isEmpty()) {
      throw new IllegalArgumentException("Loyalty tier can`t be empty");
    } else if (!Set.of("BRONZE", "SILVER", "GOLD").contains(loyaltyTier)) {
      throw new IllegalArgumentException("Invalid loyalty tier!");
    }

  }
}
