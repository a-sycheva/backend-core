package ru.mentee.power.crm.spring.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;

@WebMvcTest(LeadController.class)
public class LeadControllerUnitTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private LeadService leadService;
  @MockitoBean private CompanyService companyService;

  @Test
  void shouldThrowExceptionWhenDeleteNotExistedLead() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(post("/leads/{id}/delete", id)).andExpect(status().is4xxClientError());

    verify(leadService).findById(id);
  }

  @Test
  void shouldDeleteLeadAndRedirect() throws Exception {
    UUID id = UUID.randomUUID();
    Lead lead =
        new Lead(id, "test@example.ru", new Company("TestCorp", "TestIndustry"), LeadStatus.NEW);
    when(leadService.findById(id)).thenReturn(Optional.of(lead));
    doNothing().when(leadService).delete(id);

    mockMvc
        .perform(post("/leads/{id}/delete", id))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    verify(leadService).delete(id);
  }

  @Test
  void shouldReturnErrorWhenUpdateWithInvalidData() throws Exception {
    UUID id = UUID.randomUUID();
    Lead lead =
        new Lead(id, "test@example.ru", new Company("TestCorp", "TestIndustry"), LeadStatus.NEW);
    when(leadService.findById(id)).thenReturn(Optional.of(lead));

    mockMvc
        .perform(
            post("/leads/" + id)
                .param("email", "testexample")
                .param("companyId", UUID.randomUUID().toString())
                .param("status", "NEW"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("request", "email"));
  }

  @Test
  void shouldReturnLeadsWhenFilteredByEmail() throws Exception {
    Lead lead =
        new Lead(
            UUID.randomUUID(),
            "test@example.ru",
            new Company("TestCorp", "TestIndustry"),
            LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads(null, "test", null, null)).thenReturn(leads);

    mockMvc
        .perform(get("/leads").param("email", "test"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads))
        .andExpect(model().attribute("email", "test"));
  }

  @Test
  void shouldReturnLeadsWhenFilteredByStatus() throws Exception {
    Lead lead =
        new Lead(
            UUID.randomUUID(),
            "test@example.ru",
            new Company("TestCorp", "TestIndustry"),
            LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads(null, null, null, LeadStatus.NEW)).thenReturn(leads);

    mockMvc
        .perform(get("/leads").param("status", "NEW"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads))
        .andExpect(model().attribute("status", LeadStatus.NEW));
  }

  @Test
  void shouldReturnLeadsWhenFilteredByEmailAndStatus() throws Exception {
    Lead lead =
        new Lead(
            UUID.randomUUID(),
            "test@example.ru",
            new Company("TestCorp", "TestIndustry"),
            LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads(null, "test", null, LeadStatus.NEW)).thenReturn(leads);

    mockMvc
        .perform(get("/leads").param("status", "NEW").param("email", "test"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads))
        .andExpect(model().attribute("status", LeadStatus.NEW))
        .andExpect(model().attribute("email", "test"));
  }

  @Test
  void shouldReturnLeadsWithoutFilter() throws Exception {
    Lead lead =
        new Lead(
            UUID.randomUUID(),
            "test@example.ru",
            new Company("TestCorp", "TestIndustry"),
            LeadStatus.NEW);
    List<Lead> leads = new ArrayList<>();
    leads.add(lead);

    when(leadService.findLeads(null, null, null, null)).thenReturn(leads);

    mockMvc
        .perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("leads", leads));
  }

  @Test
  void shouldReturnFormWithErrorWhenEmailIsBlank() throws Exception {
    mockMvc
        .perform(
            post("/leads")
                .param("email", "")
                .param("companyId", UUID.randomUUID().toString())
                .param("status", "NEW"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("request", "email"));
  }

  @Test
  void shouldReturnFormWithErrorWhenEmailIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/leads")
                .param("email", "olololo")
                .param("companyId", UUID.randomUUID().toString())
                .param("status", "NEW"))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("request", "email"));
  }

  @Test
  void shouldRedirectWhenEmailIsValid() throws Exception {
    mockMvc
        .perform(
            post("/leads")
                .param("name", "Ivan")
                .param("email", "test@example.ru")
                .param("companyId", UUID.randomUUID().toString())
                .param("status", "NEW"))
        // .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/leads"));
  }

  @Test
  void shouldReturnFormWithErrorWhenStatusIsNull() throws Exception {
    mockMvc
        .perform(
            post("/leads")
                .param("email", "test@example.ru")
                .param("companyId", UUID.randomUUID().toString()))
        .andExpect(view().name("leads/form"))
        .andExpect(model().attributeHasFieldErrors("request", "status"));
  }

  @Test
  void shouldReturnHomePageWithCorrectLeadCount() throws Exception {
    List<Lead> leads =
        List.of(
            new Lead(
                UUID.randomUUID(),
                "Anna",
                "anna@test.ru",
                new Company("Corp 1", "TestIndustry"),
                LeadStatus.NEW),
            new Lead(
                UUID.randomUUID(),
                "Bob",
                "bob@test.ru",
                new Company("Corp 2", "TestIndustry"),
                LeadStatus.NEW));
    when(leadService.findAll()).thenReturn(leads);

    mockMvc
        .perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string("Spring Boot CRM is running! Leads in Database: 2 leads."));

    verify(leadService).findAll();
  }

  @Test
  void shouldShowConvertForm() throws Exception {
    UUID leadId = UUID.randomUUID();
    Lead lead =
        new Lead(leadId, "test@mail.ru", new Company("TestCorp", "TestIndustry"), LeadStatus.NEW);
    when(leadService.findById(leadId)).thenReturn(Optional.of(lead));

    mockMvc
        .perform(get("/leads/convert/{leadId}", leadId))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("lead"))
        .andExpect(view().name("leads/convert"));
  }

  @Test
  void shouldConvertLeadToDeal() throws Exception {
    UUID leadId = UUID.randomUUID();
    BigDecimal amount = BigDecimal.valueOf(50000);

    mockMvc
        .perform(
            post("/leads/convert")
                .param("leadId", leadId.toString())
                .param("amount", amount.toString()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/leads"));

    verify(leadService).convertLeadToDeal(leadId, amount);
  }
}
