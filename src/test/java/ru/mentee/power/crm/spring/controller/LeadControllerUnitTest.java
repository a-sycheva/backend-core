package ru.mentee.power.crm.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.MockLeadService;

@WebMvcTest(LeadController.class)
public class LeadControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private LeadService leadService;

  @Test
  void shouldCreateControllerWithoutSpring() {
    MockLeadService mockService = new MockLeadService();

    LeadController controller = new LeadController(mockService);

    String response = controller.home();
    assertThat(response).contains("2 leads"); // MockLeadService возвращает 2 лида
  }

  @Test
  void shouldUseInjectedService() {
    MockLeadService mockService = new MockLeadService();
    LeadController controller = new LeadController(mockService);

    String response = controller.home();

    assertThat(response).isNotNull();
    assertThat(response).contains("Spring Boot CRM is running");
  }

  @Test
  void shouldThrowExceptionWhenDeleteNotExistedLead() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(post("/leads/{id}/delete", id))
        .andExpect(status().is4xxClientError());

    verify(leadService).findById(id);
  }

  @Test
  void shouldDeleteLeadAndRedirect() throws Exception {
    UUID id = UUID.randomUUID();
    Lead lead = new Lead(id, "test@example.ru", "TestCorp", LeadStatus.NEW);
    when(leadService.findById(id)).thenReturn(Optional.of(lead));
    doNothing().when(leadService).delete(id);

    mockMvc.perform(post("/leads/{id}/delete", id))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    verify(leadService).delete(id);
  }

  @Test
  void shouldReturnErrorWhenUpdateWithInvalidData() throws Exception {
    UUID id = UUID.randomUUID();
    Lead lead = new Lead(id, "test@example.ru", "TestCorp", LeadStatus.NEW);
    when(leadService.findById(id)).thenReturn(Optional.of(lead));

    mockMvc.perform(post("/leads/" + id)
        .param("email", "testexample")
        .param("company", "TestCorp")
        .param("status", "NEW"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("lead", "email"));
  }

  @Test
  void shouldReturnLeadsWhenFilteredByEmail() throws Exception {
    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "TestCorp", LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads("test", null, null))
        .thenReturn(leads);

    mockMvc.perform(get("/leads").param("email", "test"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads))
        .andExpect(model().attribute("email", "test"));
  }

  @Test
  void shouldReturnLeadsWhenFilteredByStatus() throws Exception {
    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "TestCorp", LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads(null, null, LeadStatus.NEW))
        .thenReturn(leads);

    mockMvc.perform(get("/leads").param("status", "NEW"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads))
        .andExpect(model().attribute("status", LeadStatus.NEW));
  }

  @Test
  void shouldReturnLeadsWhenFilteredByEmailAndStatus() throws Exception {
    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "TestCorp", LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads("test", null, LeadStatus.NEW))
        .thenReturn(leads);

    mockMvc.perform(get("/leads").
            param("status", "NEW").param("email", "test"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads))
        .andExpect(model().attribute("status", LeadStatus.NEW))
        .andExpect(model().attribute("email", "test"));
  }

  @Test
  void shouldReturnLeadsWithoutFilter() throws Exception {
    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "TestCorp", LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads(null, null, null))
        .thenReturn(leads);

    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads));
  }

  @Test
  void shouldReturnFormWithErrorWhenEmailIsBlank() throws Exception {
    mockMvc.perform(post("/leads").
        param("email", "")
        .param("company", "TestCorp")
        .param("status", "NEW"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("lead", "email"));
  }

  @Test
  void shouldReturnFormWithErrorWhenEmailIsInvalid() throws Exception {
    mockMvc.perform(post("/leads").
            param("email", "ololo@ololo")
            .param("company", "TestCorp")
            .param("status", "NEW"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("lead", "email"));
  }

  @Test
  void shouldRedirectWhenEmailIsValid() throws Exception {
    mockMvc.perform(post("/leads").
            param("email", "test@example.ru")
            .param("company", "TestCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/leads"));
  }

  @Test
  void shouldReturnFormWithErrorWhenCompanyIsBlank() throws Exception {
    mockMvc.perform(post("/leads").
            param("email", "test@example.ru")
            .param("company", "")
            .param("status", "NEW"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("lead", "company"));
  }

  @Test
  void shouldReturnFormWithErrorWhenStatusIsNull() throws Exception {
    mockMvc.perform(post("/leads")
            .param("email", "test@example.ru")
            .param("company", "TestCorp"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("lead", "status"));
  }
}
