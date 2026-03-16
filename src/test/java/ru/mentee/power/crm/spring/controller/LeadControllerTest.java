package ru.mentee.power.crm.spring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LeadControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldReturnHtmlTableWhenDoGetCalled() throws Exception {

    mockMvc.perform(get("/leads"))
            .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email")));
  }

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
            .param("email", "test@example.ru")
            .param("company", "TestCorp")
            .param("status", "NEW"))
        .andExpect(status().is3xxRedirection())           // статус 302 (редирект)
        .andExpect(header().string("Location", "/leads")); // куда редирект
  }
}