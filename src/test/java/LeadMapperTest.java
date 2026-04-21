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
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
class LeadMapperTest {

  @Autowired private LeadMapper leadMapper;

  @Test
  void shouldMapCreateRequestToEntityWhenValidData() {
    CreateLeadRequest request =
        new CreateLeadRequest("Billy", "test@test.ru", null, LeadStatus.NEW);

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

    assertThat(response.id()).isEqualTo(lead.getId());
    assertThat(response.name()).isEqualTo(lead.getName());
    assertThat(response.email()).isEqualTo(lead.getEmail());
    assertThat(response.status()).isEqualTo(lead.getStatus());
    assertThat(response.companyId()).isEqualTo(lead.getCompany().getId());
    assertThat(response.companyName()).isEqualTo(lead.getCompany().getName());
  }

  @Test
  void shouldUpdateLead() {
    UpdateLeadRequest request =
        new UpdateLeadRequest("Billy", "test@test.ru", null, LeadStatus.NEW);

    Lead lead = new Lead("Anna", "updated@test.ru", null, LeadStatus.NEW);

    leadMapper.updateEntity(request, lead);

    assertThat(lead.getName()).isEqualTo(request.getName());
    assertThat(lead.getEmail()).isEqualTo(request.getEmail());
    assertThat(lead.getStatus()).isEqualTo(request.getStatus());
  }
}
