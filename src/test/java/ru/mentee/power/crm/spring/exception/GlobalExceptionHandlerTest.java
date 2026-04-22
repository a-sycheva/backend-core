package ru.mentee.power.crm.spring.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.LeadRestController;

@WebMvcTest(LeadRestController.class)
public class GlobalExceptionHandlerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private LeadService mockLeadService;

  @MockitoBean
  private LeadMapper mockLeadMapper;

  @Test
  void shouldReturn404WhenEntityNotFound() throws Exception {
    UUID id = UUID.randomUUID();
    when(mockLeadService.getLeadById(id))
        .thenThrow(new EntityNotFoundException("Lead", id.toString()));

    mockMvc.perform(get("/api/leads/" + id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value("404"))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id));
  }

  @Test
  void shouldReturn400WithFieldErrorsWhenValidationFals() throws Exception {
    String jsonBody = """
        {
            "name": "Anastasiya",
            "email": "not-an-email",
            "status": null
        }
        """;

    mockMvc.perform(post("/api/leads")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonBody))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errors").exists())
        .andExpect(jsonPath("$.errors.email").value("Некорректный формат email"))
        .andExpect(jsonPath("$.errors.status").value("Указать статус обязательно"));
  }

  @Test
  void shouldReturn500WhenUnexpectedExceptionOccurs() throws Exception {
    UUID id = UUID.randomUUID();
    when(mockLeadService.getLeadById(id))
        .thenThrow(new RuntimeException());

    mockMvc.perform(get("/api/leads/" + id))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Internal Server Error"))
        .andExpect(jsonPath("$.message").value("Internal Server Error"));
  }

}
