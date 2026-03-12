package ru.mentee.power.crm;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;
import ru.mentee.power.crm.spring.Application;

class StackComparisonTest {

  private static final int SERVLET_PORT = 8080;
  private static final int SPRING_PORT = 8081;

  private HttpClient httpClient;

  @Autowired
  private LeadService springLeadService;

  @BeforeEach
  void setUp() {
    httpClient = HttpClient.newHttpClient();
  }

  @Test
  @DisplayName("Оба стека должны возвращать лидов в HTML таблице")
  void shouldReturnLeadsFromBothStacks() throws Exception {

    //запуск servlet-сервера
    LeadRepository leadRepository = new InMemoryLeadRepository();

    LeadService servletLeadService = new LeadService(leadRepository);

    addTestLeads(servletLeadService);

    Tomcat tomcat = new Tomcat();
    tomcat.setPort(SERVLET_PORT);

    Context servletContext = tomcat.addContext("", new File(".").getAbsolutePath());
    servletContext.getServletContext().setAttribute("LeadService", servletLeadService);

    tomcat.addServlet(servletContext, "LeadListServlet", new LeadListServlet());
    servletContext.addServletMappingDecoded("/leads", "LeadListServlet");

    tomcat.start();
    System.out.println("=== Tomcat started at port "
        + tomcat.getConnector().getLocalPort() + "===");
    Thread.sleep(2000); //ожидание запуска servlet-сервера

    //запуск spring-сервера
    SpringApplication app = new SpringApplication(Application.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", SPRING_PORT));

    ConfigurableApplicationContext springContext = app.run();
    Thread.sleep(2000); //ожидание запуска spring-сервера

    LeadService springLeadService = springContext.getBean(LeadService.class);

    addTestLeads(springLeadService);

    HttpRequest servletRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + SERVLET_PORT + "/leads"))
        .GET()
        .build();

    HttpRequest springRequest = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + SPRING_PORT + "/leads"))
        .GET()
        .build();

    // When: выполняем запросы
    HttpResponse<String> servletResponse = httpClient.send(
        servletRequest, HttpResponse.BodyHandlers.ofString());
    HttpResponse<String> springResponse = httpClient.send(
        springRequest, HttpResponse.BodyHandlers.ofString());

    // Then: оба возвращают 200 OK и содержат таблицу
    assertThat(servletResponse.statusCode()).isEqualTo(200);
    assertThat(springResponse.statusCode()).isEqualTo(200);

    assertThat(servletResponse.body()).contains("<table");
    assertThat(springResponse.body()).contains("<table");

    int servletRows = countTableRows(servletResponse.body());
    int springRows = countTableRows(springResponse.body());

    assertThat(servletRows)
        .as("Количество лидов должно совпадать")
        .isEqualTo(springRows);

    System.out.printf("Servlet: %d лидов, Spring: %d лидов%n",
        servletRows, springRows);

    tomcat.stop();
    springContext.close();
  }

  void addTestLeads(LeadService leadService) {
    leadService.addLead("text1@example.ru", "FirstComp", LeadStatus.NEW);
    leadService.addLead("text2@example.ru", "SecondComp", LeadStatus.NEW);
    leadService.addLead("text3@example.ru", "ThirdComp", LeadStatus.NEW);
    leadService.addLead("text4@example.ru", "ForthComp", LeadStatus.NEW);
    leadService.addLead("text5@example.ru", "FivesComp", LeadStatus.NEW);
    leadService.addLead("<script>alert('XSS')</script>", "XssComp", LeadStatus.NEW);

  }

  private int countTableRows(String html) {
    Pattern pattern = Pattern.compile("<tr\\b[^>]*>");
    Matcher matcher = pattern.matcher(html);

    int count = 0;
    while (matcher.find()) {
      count++;
    }
    return count - 1; //убрать из количества строку с заголовком таблицы
  }

  @Test
  @DisplayName("Измерение времени старта обоих стеков")
  void shouldMeasureStartupTime() throws LifecycleException {
    long servletStartupMs = measureServletStartup();

    long springStartupMs = measureSpringBootStartup();

    System.out.println("=== Сравнение времени старта ===");
    System.out.printf("Servlet стек: %d ms%n", servletStartupMs);
    System.out.printf("Spring Boot: %d ms%n", springStartupMs);
    System.out.printf("Разница: Spring %s на %d ms%n",
        springStartupMs > servletStartupMs ? "медленнее" : "быстрее",
        Math.abs(springStartupMs - servletStartupMs));

    assertThat(servletStartupMs).isLessThan(10_000);
    assertThat(springStartupMs).isLessThan(15_000);
  }

  private long measureServletStartup() throws LifecycleException {

    LeadRepository leadRepository = new InMemoryLeadRepository();

    LeadService leadService = new LeadService(leadRepository);

    long servletStart = System.nanoTime();
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(0);

    Context context = tomcat.addContext("", new File(".").getAbsolutePath());
    context.getServletContext().setAttribute("LeadService", leadService);

    tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
    context.addServletMappingDecoded("/leads", "LeadListServlet");

    tomcat.start();

    long servletEnd = System.nanoTime();

    tomcat.stop();

    return  (servletEnd - servletStart) / 1_000_000;
  }

  private long measureSpringBootStartup() {
    long springStart = System.nanoTime();

    SpringApplication app = new SpringApplication(Application.class);
    app.setDefaultProperties(Collections.singletonMap("server.port", "0"));

    ConfigurableApplicationContext context = app.run();

    long springEnd = System.nanoTime();

    context.close();

    return  (springEnd - springStart) / 1_000_000;
  }
}