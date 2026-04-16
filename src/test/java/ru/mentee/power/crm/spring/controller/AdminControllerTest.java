package ru.mentee.power.crm.spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.model.Product;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.service.ProductService;

@WebMvcTest(AdminController.class)
@ActiveProfiles("test")
class AdminControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private LeadService leadService;
  @MockitoBean private DealService dealService;
  @MockitoBean private ProductService productService;

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
        .perform(get("/admin"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .string("Spring Boot CRM is running! " + "Admin controller is OK. Total leads: 2"));

    verify(leadService).findAll();
  }

  @Test
  void shouldAddTestDataWhenDatabaseIsEmpty() throws Exception {
    when(leadService.findAll()).thenReturn(Collections.emptyList());

    // when & then
    mockMvc
        .perform(post("/admin/addTestLeads"))
        .andExpect(status().isOk())
        .andExpect(content().string("All leads added!"));

    verify(leadService, times(1)).addLead("Ivan", "test1@example.ru", null, LeadStatus.NEW);
    verify(leadService, times(1)).addLead("Anastasiya", "test2@example.ru", null, LeadStatus.NEW);
    verify(leadService, times(1)).addLead("Konstantin", "test3@example.ru", null, LeadStatus.NEW);
    verify(leadService, times(1)).addLead("Nataliya", "test4@example.ru", null, LeadStatus.NEW);
  }

  @Test
  void shouldAddTestProductsWhenDatabaseIsEmpty() throws Exception {
    when(productService.findAll()).thenReturn(Collections.emptyList());

    mockMvc
        .perform(post("/admin/addTestProducts"))
        .andExpect(status().isOk())
        .andExpect(content().string("All products added!"));

    verify(productService, times(1))
        .addProduct(
            "Консультация по архитектуре", "CONSULT-ARCH-001", new BigDecimal("50000.00"), true);
    verify(productService, times(1))
        .addProduct(
            "Консультация по бизнес-аналитике", "CONSULT-BI-001", new BigDecimal("70000.00"), true);
    verify(productService, times(1))
        .addProduct("ноутбук DELL", "LAPTOP-001", new BigDecimal("90000.00"), true);
    verify(productService, times(1))
        .addProduct(
            "Подписка Microsoft 365 Business", "SOFT-MSFT-001", new BigDecimal("8900.00"), true);
    verify(productService, times(1))
        .addProduct(
            "Оптимизация SQL запросов и производительности БД",
            "CONSULT-DB-001",
            new BigDecimal("80000.00"),
            true);
  }

  @Test
  void shouldAddTestDealWhenDatabaseIsEmpty() throws Exception {
    when(dealService.getAllDeals()).thenReturn(Collections.emptyList());

    Lead testLead = new Lead();
    testLead.setId(UUID.randomUUID());
    testLead.setName("Tom");

    List<Product> testProducts =
        List.of(
            new Product(
                "Консультация по архитектуре",
                "CONSULT-ARCH-001",
                new BigDecimal("50000.00"),
                true),
            new Product(
                "Консультация по бизнес-аналитике",
                "CONSULT-BI-001",
                new BigDecimal("70000.00"),
                true),
            new Product("ноутбук DELL", "LAPTOP-001", new BigDecimal("90000.00"), true));

    when(leadService.findAll()).thenReturn(List.of(testLead));
    when(productService.findAll()).thenReturn(testProducts);

    mockMvc
        .perform(post("/admin/addTestDeal"))
        .andExpect(status().isOk())
        .andExpect(content().string("All deals added!"));

    verify(dealService, times(2)).addDeal(any(Deal.class));
  }

  @Test
  void shouldNotAddTestLeadsWhenDatabaseAlreadyHasData() throws Exception {
    Lead existingLead = new Lead();

    when(leadService.findAll()).thenReturn(List.of(existingLead));

    mockMvc
        .perform(post("/admin/addTestLeads"))
        .andExpect(status().isOk())
        .andExpect(content().string("All leads added!")); // сообщение то же самое

    verify(leadService, never()).addLead(anyString(), anyString(), any(), any(LeadStatus.class));
  }

  @Test
  void shouldNotAddTestProductsWhenDatabaseAlreadyHasData() throws Exception {
    Product existingProduct = new Product();

    when(productService.findAll()).thenReturn(List.of(existingProduct));

    mockMvc
        .perform(post("/admin/addTestProducts"))
        .andExpect(status().isOk())
        .andExpect(content().string("All products added!"));

    verify(productService, never())
        .addProduct(anyString(), anyString(), any(BigDecimal.class), anyBoolean());
  }

  @Test
  void shouldNotAddTestDealWhenDatabaseAlreadyHasData() throws Exception {
    Deal existingDeal = new Deal();

    when(dealService.getAllDeals()).thenReturn(List.of(existingDeal));

    mockMvc
        .perform(post("/admin/addTestDeal"))
        .andExpect(status().isOk())
        .andExpect(content().string("All deals added!"));

    verify(dealService, never()).addDeal(any(Deal.class));
  }
}
