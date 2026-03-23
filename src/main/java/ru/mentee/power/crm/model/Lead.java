package ru.mentee.power.crm.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Lead(UUID id,
                   @NotBlank(message = "Email обязателен")
                   @Email(regexp = ".+@.+\\..+", message = "Некорректный формат email")
                   String email,
                   @NotBlank(message = "Указать компанию обязательно")
                   String company,
                   @NotNull(message = "Указать статус обязательно")
                   LeadStatus status) {

  @Override
  public boolean equals(Object o) {
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
