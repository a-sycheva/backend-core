package ru.mentee.power.crm.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@ExtendWith(MockitoExtension.class)
class LeadListServletTest {
  private LeadListServlet servlet;

  @Mock
  private HttpServletResponse mockResp;
  @Mock
  private HttpServletRequest mockReq;
  @Mock
  private ServletContext mockContext;
  @Mock
  private LeadService mockService;

  @BeforeEach
  void setUp () {
    servlet = new LeadListServlet() {
      @Override
      public ServletContext getServletContext() {
        return mockContext;
      }
    };
  }

  @Test
  void shouldReturnHtmlTableWhenDoGetCalled() throws Exception {

    Lead firstLead = new Lead(UUID.randomUUID(), "test1@example.ru",
        "FirstCompany", LeadStatus.NEW);
    Lead secondLead = new Lead(UUID.randomUUID(), "test1@example.ru",
        "FirstCompany", LeadStatus.NEW);
    List<Lead> leads = new ArrayList<Lead>();
    leads.add(firstLead);
    leads.add(secondLead);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(mockResp.getWriter()).thenReturn(printWriter);

    when(mockContext.getAttribute("LeadService")).thenReturn(mockService);
    when(mockService.findAll()).thenReturn(leads);

    servlet.doGet(mockReq, mockResp);

    verify(mockService).findAll();

    String htmlResp = stringWriter.toString();
    assertThat(htmlResp).contains("<head><title>CRM - Lead List</title></head>")
        .contains("<th>Email</th>").
        contains("<td>" + firstLead.email() + "</td>").
        contains("<td>" + secondLead.email() + "</td>").
        contains("</html>");
  }

  @Test
  void shouldSetContentTypeToHtmlWhenDoGetCalled() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(mockResp.getWriter()).thenReturn(printWriter);

    when(mockContext.getAttribute("LeadService")).thenReturn(mockService);

    servlet.doGet(mockReq, mockResp);

    verify(mockContext).getAttribute("LeadService");
    verify(mockResp).setContentType("text/html; charset=UTF-8");
    verify(mockResp).setStatus(200);
  }
}