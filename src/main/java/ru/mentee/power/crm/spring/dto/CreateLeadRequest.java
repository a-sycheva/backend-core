package ru.mentee.power.crm.spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreateLeadRequest {
  @NotBlank(message = "Указать имя обязательно")
  @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
  private String name;

  @NotBlank(message = "{Email обязателен")
  @Email(regexp = ".+@.+\\..+", message = "Некорректный формат email")
  private String email;

  private UUID companyId;

  @NotNull(message = "Указать статус обязательно")
  private LeadStatus status;
}
