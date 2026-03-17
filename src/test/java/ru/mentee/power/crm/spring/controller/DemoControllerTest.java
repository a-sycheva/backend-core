package ru.mentee.power.crm.spring.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;

@WebMvcTest(DemoController.class)
class DemoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LeadService constructorService;  // для Constructor Injection

  @MockitoBean
  private LeadRepository fieldRepository;  // для Field Injection

  @Test
  void shouldShowDIStatus() throws Exception {
    mockMvc.perform(get("/demo"))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("Constructor Injection (final): ✓ Injected")))
        .andExpect(content().string(containsString("Field Injection (@Autowired field): ✓ Injected")))
        .andExpect(content().string(containsString("Setter Injection (@Autowired setter): ✓ Injected")))
        .andExpect(content().string(containsString("Recommendation: Use Constructor Injection with final fields.")));
  }
}