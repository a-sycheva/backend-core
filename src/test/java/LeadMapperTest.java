import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.Application;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class LeadMapperTest {

  @Autowired private LeadMapper leadMapper;

  @Test
  void shouldMapCreateRequestToEntityWhenValidData() {
    CreateLeadRequest request =
        new CreateLeadRequest(
            "Billy", "test@test.ru", ru.mentee.power.crm.spring.dto.generated.LeadStatus.NEW);

    Lead lead = leadMapper.toEntity(request);

    assertThat(lead.getName()).isEqualTo(request.getName());
    assertThat(lead.getEmail()).isEqualTo(request.getEmail());
    assertThat(lead.getId()).isNull();
  }

  @Test
  void shouldMapEntityToResponseWhenValidEntity() {
    Company company = new Company("Yandex", "IT");

    Lead lead = new Lead(UUID.randomUUID(), "Billy", "test@test.ru", company, LeadStatus.NEW);
    LeadResponse response = leadMapper.toResponse(lead);

    assertThat(response.getId()).isEqualTo(lead.getId());
    assertThat(response.getName()).isEqualTo(lead.getName());
    assertThat(response.getEmail()).isEqualTo(lead.getEmail());
    assertThat(response.getStatus()).isEqualTo(map(lead.getStatus()));
    assertThat(response.getCompanyId()).isEqualTo(lead.getCompany().getId());
    assertThat(response.getCompanyName()).isEqualTo(lead.getCompany().getName());
  }

  @Test
  void shouldUpdateLead() {
    UpdateLeadRequest request =
        new UpdateLeadRequest(
            "Billy", "test@test.ru", ru.mentee.power.crm.spring.dto.generated.LeadStatus.NEW);

    Lead lead = new Lead("Anna", "updated@test.ru", null, LeadStatus.NEW);

    leadMapper.updateEntity(request, lead);

    assertThat(lead.getName()).isEqualTo(request.getName());
    assertThat(lead.getEmail()).isEqualTo(request.getEmail());
    assertThat(lead.getStatus()).isEqualTo(map(request.getStatus()));
  }

  // вспомогательные методы
  private ru.mentee.power.crm.model.LeadStatus map(
      ru.mentee.power.crm.spring.dto.generated.LeadStatus status) {
    return status == null ? null : ru.mentee.power.crm.model.LeadStatus.valueOf(status.name());
  }

  private ru.mentee.power.crm.spring.dto.generated.LeadStatus map(
      ru.mentee.power.crm.model.LeadStatus status) {
    return status == null
        ? null
        : ru.mentee.power.crm.spring.dto.generated.LeadStatus.valueOf(status.name());
  }
}
