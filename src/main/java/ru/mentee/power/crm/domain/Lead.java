package ru.mentee.power.crm.domain;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record Lead (UUID id,
                    Contact contact,
                    String company,
                    String status) {

  public Lead {
    if (id == null) {
      throw new IllegalArgumentException("Id can`t be empty");
    }

    if (status == null || status.isEmpty()) {
      throw new IllegalArgumentException("Status can`t be empty");
    } else if (!Set.of("NEW", "QUALIFIED", "CONVERTED").contains(status)) {
      throw new IllegalArgumentException("Invalid status!");
    }

    if (contact == null) {
      throw new IllegalArgumentException("Contact can`t be empty");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Lead lead = (Lead) o;
    return Objects.equals(id, lead.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
