package ru.mentee.power.crm.spring.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
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
    CreateLeadRequest request = new CreateLeadRequest("Anton", "", null, LeadStatus.NEW);
    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenEmailHasInvalidFormat() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest("Anton", "test@test", null, LeadStatus.NEW);
    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn400WhenNameIsTooShort() throws Exception {
    CreateLeadRequest request = new CreateLeadRequest("A", "test@test.ru", null, LeadStatus.NEW);
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
            lead.getCompany().getId(),
            lead.getCompany().getName(),
            lead.getStatus(),
            lead.getCreatedAt());
    when(mockLeadMapper.toResponse(any())).thenReturn(response);

    CreateLeadRequest request =
        new CreateLeadRequest("Anton", "test@test.ru", null, LeadStatus.NEW);
    String requestJson = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isCreated());
  }
}
