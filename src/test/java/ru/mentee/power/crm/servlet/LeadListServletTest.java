package ru.mentee.power.crm.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
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

  TemplateEngine templateEngine;

  @BeforeEach
  void setUp () {
    servlet = new LeadListServlet() {
      @Override
      public ServletContext getServletContext() {
        return mockContext;
      }
    };
    Path templatePath = Path.of("src/main/jte");
    DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatePath);
    templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
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

    servlet.templateEngine = templateEngine;

    servlet.doGet(mockReq, mockResp);

    verify(mockService).findAll();

    String htmlResp = stringWriter.toString();
    assertThat(htmlResp).contains("<title>CRM - Lead Management</title>")
        .contains("Email</th>").
        contains(firstLead.email() + "</td>").
        contains(secondLead.email() + "</td>").
        contains("</html>");
  }

  @Test
  void shouldSetContentTypeToHtmlWhenDoGetCalled() throws Exception {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(mockResp.getWriter()).thenReturn(printWriter);
    when(mockContext.getAttribute("LeadService")).thenReturn(mockService);

    servlet.templateEngine = templateEngine;

    servlet.doGet(mockReq, mockResp);

    verify(mockContext).getAttribute("LeadService");
    verify(mockResp).setContentType("text/html; charset=UTF-8");
    verify(mockResp).setStatus(200);
  }

  @Test
  void shouldInitializeEngine() {
    LeadListServlet servlet = new LeadListServlet();

    servlet.init();

    assertThat(servlet.getTemplateEngine()).isNotNull();
  }
}