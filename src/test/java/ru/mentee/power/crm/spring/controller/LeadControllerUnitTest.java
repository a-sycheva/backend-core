package ru.mentee.power.crm.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
