package ru.mentee.power.crm.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mentee.power.crm.model.InviteeStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInviteeRequest {
  @NotBlank(message = "Имя обязательно")
  String firstName;

  @NotBlank(message = "Email обязателен")
  @Email(regexp = ".+@.+\\..+", message = "Некорректный формат email")
  String email;

  @NotNull(message = "Статус обязателен")
  InviteeStatus status;
}
