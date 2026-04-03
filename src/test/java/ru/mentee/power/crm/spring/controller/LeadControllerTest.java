package ru.mentee.power.crm.spring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LeadControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private LeadService service;

  @ParameterizedTest
  @CsvSource({
      "NEW",
      "CONTACTED",
      "QUALIFIED"
  })
  void shouldReturnHtmlTableWhenDoGetCalledWithParam(String status) throws Exception {

    mockMvc.perform(get("/leads?status=" + status))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Email")));
  }

  @Test
  void shouldReturnLeadAddFormWhenDoGetCalled() throws Exception {

    mockMvc.perform(get("/leads/new"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("name=\"email\"")));
  }

  @Test
  void shouldRedirectWhenAddLead() throws Exception {
    mockMvc.perform(post("/leads")
            .param("name", "Dexter")
            .param("email", "test@example.ru")
            .param("company", "TestCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection())           // статус 302 (редирект)
        .andExpect(header().string("Location", "/leads")); // куда редирект
  }

  @Test
  void shouldShowEditForm() throws Exception {

    Lead lead = service.addLead("Dexter", "test1@example.ru",
        "TestCorp", LeadStatus.NEW);

    mockMvc.perform(get("/leads/" + lead.id() + "/edit"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("lead", lead))
        .andExpect(view().name("leads/edit"))
        .andExpect(content().string((containsString("Редактирование лида"))))
        .andExpect(content().string(containsString("test1@example.ru")));
  }

  @Test
  void shouldReturn404ForNonexistentId() throws Exception {
    mockMvc.perform(post("/leads/" + UUID.randomUUID()))
        .andExpect(status().isNotFound());

    mockMvc.perform(get("/leads/" + UUID.randomUUID() + "/edit"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldUpdateLead() throws Exception {
    Lead lead = service.addLead("Dexter", "old@example.ru",
        "TestCorp", LeadStatus.NEW);

    mockMvc.perform(post("/leads/" + lead.id())
            .param("name", "Joys")
            .param("email", "new@example.ru")
        .param("company", "TestCorp")
        .param("status", "CONTACTED"))
        .andExpect(status().is3xxRedirection())
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "/leads"));

    mockMvc.perform(get("/leads"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("new@example.ru")))
        .andExpect(content().string(containsString("CONTACTED")));
  }
}