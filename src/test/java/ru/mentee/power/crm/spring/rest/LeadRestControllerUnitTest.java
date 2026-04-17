package ru.mentee.power.crm.spring.rest;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@WebMvcTest(LeadRestController.class)
@ActiveProfiles("test")
class LeadRestControllerUnitTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private LeadService leadService;

  @Test
  void getAllLeadsShouldReturnListOfLeads() throws Exception {
    // given
    Lead firstLead = new Lead("John", "john@test.ru", null, LeadStatus.NEW);
    Lead secondLead = new Lead("Jane", "jane@test.ru", null, LeadStatus.CONTACTED);
    when(leadService.findAll()).thenReturn(List.of(firstLead, secondLead));

    // when & then
    mockMvc
        .perform(get("/api/leads"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("John"))
        .andExpect(jsonPath("$[1].name").value("Jane"));
  }

  @Test
  void getLeadByIdWhenExistsShouldReturnLead() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    Lead lead = new Lead("John", "john@test.ru", null, LeadStatus.NEW);
    when(leadService.findById(id)).thenReturn(Optional.of(lead));

    // when & then
    mockMvc
        .perform(get("/api/leads/{id}", id))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("John"));
  }

  @Test
  void getLeadByIdWhenNotExistsShouldReturnNotFound() throws Exception {
    // given
    UUID id = UUID.randomUUID();
    when(leadService.findById(id)).thenReturn(Optional.empty());

    // when & then
    mockMvc.perform(get("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void createLeadShouldCreateAndReturnLead() throws Exception {
    // given
    Lead requestLead = new Lead("John", "john@test.ru", null, LeadStatus.NEW);
    Lead savedLead = new Lead("John", "john@test.ru", null, LeadStatus.NEW);

    when(leadService.addLead(any(), any(), any(), any())).thenReturn(savedLead);

    // when & then
    mockMvc
        .perform(
            post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestLead)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("John"));
  }

  @Test
  void shouldReturn200WhenGetAllLeads() throws Exception {
    Lead firstLead = new Lead("John", "john@test.ru", null, LeadStatus.NEW);
    Lead secondLead = new Lead("Jane", "jane@test.ru", null, LeadStatus.CONTACTED);
    when(leadService.findAll()).thenReturn(List.of(firstLead, secondLead));

    // when & then
    mockMvc
        .perform(get("/api/leads"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void shouldReturn404WhenGetNonExistentLead() throws Exception {
    when(leadService.findById(any(UUID.class))).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/api/leads/00000000-0000-0000-0000-000000000000"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocationWhenCreateLead() throws Exception {
    Lead lead = new Lead(UUID.randomUUID(), "Anton", "test@example.ru", null, LeadStatus.NEW);

    when(leadService.addLead(lead.getName(), lead.getEmail(), null, lead.getStatus()))
        .thenReturn(lead);

    mockMvc
        .perform(
            post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lead)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(header().string("Location", containsString("/api/leads/" + lead.getId())));
  }

  @Test
  void shouldReturn204WhenDeleteExistingLead() throws Exception {
    when(leadService.deleteLead(any(UUID.class))).thenReturn(true);

    mockMvc
        .perform(delete("/api/leads/" + UUID.randomUUID()))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void shouldReturn404WhenDeleteNonExistentLead() throws Exception {
    when(leadService.deleteLead(any(UUID.class))).thenReturn(false);

    mockMvc
        .perform(delete("/api/leads/00000000-0000-0000-0000-000000000000"))
        .andExpect(status().isNotFound());
  }
}
