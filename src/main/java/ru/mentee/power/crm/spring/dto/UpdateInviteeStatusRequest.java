package ru.mentee.power.crm.spring.dto;

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
public class UpdateInviteeStatusRequest {
  @NotNull(message = "Статус обязателен")
  InviteeStatus status;
}
