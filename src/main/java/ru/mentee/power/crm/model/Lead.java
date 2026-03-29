package ru.mentee.power.crm.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leads")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public  class Lead {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private  UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private  @NotBlank(message = "Email обязателен")
  @Email(regexp = ".+@.+\\..+", message = "Некорректный формат email") String email;

  @Column(nullable = false)
  private  @NotBlank(message = "Указать компанию обязательно") String company;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private  @NotNull(message = "Указать статус обязательно") LeadStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public Lead(UUID id,
              @NotBlank(message = "Email обязателен")
              @Email(regexp = ".+@.+\\..+", message = "Некорректный формат email")
              String email,
              @NotBlank(message = "Указать компанию обязательно")
              String company,
              @NotNull(message = "Указать статус обязательно")
              LeadStatus status) {
    this.id = id;
    this.email = email;
    this.company = company;
    this.status = status;
  }

  //конструктор для старых тестов
  public Lead(String email, String company, LeadStatus status) {
    this.email = email;
    this.company = company;
    this.status = status;
  }

  public Lead(String name, String email, String company, LeadStatus status) {
    this.name = name;
    this.email = email;
    this.company = company;
    this.status = status;
  }

  public UUID id() {
    return id;
  }

  public @NotBlank(message = "Name обязателен") String name() {
    return name;
  }

  public @NotBlank(message = "Email обязателен") @Email(regexp = ".+@.+\\..+", message = "Некорректный формат email") String email() {
    return email;
  }

  public @NotBlank(message = "Указать компанию обязательно") String company() {
    return company;
  }

  public @NotNull(message = "Указать статус обязательно") LeadStatus status() {
    return status;
  }

  @Override
  public String toString() {
    return "Lead[" +
        "id=" + id + ", " +
        "email=" + email + ", " +
        "company=" + company + ", " +
        "status=" + status + ']';
  }
}
