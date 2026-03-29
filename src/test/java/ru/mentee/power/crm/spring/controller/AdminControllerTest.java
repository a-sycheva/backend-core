package ru.mentee.power.crm.spring.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private LeadService leadService;

  @Test
  void shouldReturnHomePageWithCorrectLeadCount() throws Exception {
    List<Lead> leads = List.of(
        new Lead(UUID.randomUUID(), "Anna", "anna@test.ru", "Corp1", LeadStatus.NEW),
        new Lead(UUID.randomUUID(), "Bob", "bob@test.ru", "Corp2", LeadStatus.NEW)
    );
    when(leadService.findAll()).thenReturn(leads);

    mockMvc.perform(get("/admin"))
        .andExpect(status().isOk())
        .andExpect(content().string("Spring Boot CRM is running! Admin controller is OK. Total leads: 2"));

    verify(leadService).findAll();
  }


  @Test
  void shouldAddTestDataWhenDatabaseIsEmpty() throws Exception {
    when(leadService.findAll()).thenReturn(Collections.emptyList());

    // when & then
    mockMvc.perform(post("/admin/addTestData"))
        .andExpect(status().isOk())
        .andExpect(content().string("Data is added!"));

    verify(leadService, times(1)).addLead("Ivan", "test1@example.ru", "FirstCorp", LeadStatus.NEW);
    verify(leadService, times(1)).addLead("Anastasiya", "test2@example.ru", "SecondCorp", LeadStatus.NEW);
    verify(leadService, times(1)).addLead("Konstantin", "test3@example.ru", "ThirdCorp", LeadStatus.NEW);
    verify(leadService, times(1)).addLead("Nataliya", "test4@example.ru", "FourthCorp", LeadStatus.NEW);
  }

  @Test
  void shouldNotAddTestDataWhenDatabaseAlreadyHasData() throws Exception {
    List<Lead> existingLeads = List.of(
        new Lead(UUID.randomUUID(), "Ivan", "test1@example.ru", "FirstCorp", LeadStatus.NEW)
    );
    when(leadService.findAll()).thenReturn(existingLeads);

    mockMvc.perform(post("/admin/addTestData"))
        .andExpect(status().isOk())
        .andExpect(content().string("Database already has data!"));
  }
}