package ru.mentee.power.crm.spring.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

@WebMvcTest(LeadRestController.class)
@ActiveProfiles("test")
public class LeadRestControllerValidationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private LeadService mockLeadService;

  @MockitoBean private LeadMapper mockLeadMapper;

  @Test
  void shouldReturn400WhenEmailIsBlank() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest("Anton", "test", map(LeadStatus.NEW));
    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenEmailHasInvalidFormat() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest("Anton", "test@test", map(LeadStatus.NEW));
    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenNameIsTooShort() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest("", "test@test.ru", map(LeadStatus.NEW));
    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn201WhenAllFieldsAreValid() throws Exception {
    // given
    Company company = new Company("Yandex", "IT");
    Lead lead = new Lead("John", "john@test.ru", company, LeadStatus.NEW);
    when(mockLeadMapper.toEntity(any())).thenReturn(lead);
    when(mockLeadService.addLead(any(), any(), any(), any())).thenReturn(lead);
    LeadResponse response =
        new LeadResponse(
            lead.getId(),
            lead.getName(),
            lead.getEmail(),
            map(lead.getStatus()),
            map(lead.getCreatedAt()));
    response.setCompanyId(lead.getCompany().getId());
    response.setCompanyName(lead.getCompany().getName());
    when(mockLeadMapper.toResponse(any())).thenReturn(response);

    CreateLeadRequest request = new CreateLeadRequest("Anton", "test@test.ru", map(LeadStatus.NEW));
    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated());
  }

  // вспомогательные методы
  private OffsetDateTime map(LocalDateTime value) {
    return value != null ? value.atOffset(ZoneOffset.UTC) : null;
  }

  private LocalDateTime map(OffsetDateTime value) {
    return value != null ? value.toLocalDateTime() : null;
  }

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
