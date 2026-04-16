package ru.mentee.power.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mentee.power.crm.model.LeadStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeadUpdateRequest {
  private UUID id;

  @NotBlank(message = "Указать имя обязательно")
  private String name;

  @NotBlank(message = "{Email обязателен")
  @Email(message = ".+@.+\\\\..+\", message = \"Некорректный формат email")
  private String email;

  private UUID companyId;

  @NotNull(message = "Указать статус обязательно")
  private LeadStatus status;
}
