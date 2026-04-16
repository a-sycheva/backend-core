package ru.mentee.power.crm.spring.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealProduct;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.model.Product;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.service.ProductService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final LeadService leadService;
  private final DealService dealService;
  private final ProductService productService;

  @GetMapping
  @ResponseBody
  public String home() {
    return "Spring Boot CRM is running! Admin controller is OK. "
        + "Total leads: "
        + leadService.findAll().size();
  }

  // для curl.exe -X POST http://localhost:8081/admin/addTestLeads
  @PostMapping("/addTestLeads")
  @ResponseBody
  public String addTestLeads(Model model) {
    // добавление лидов
    if (leadService.findAll().isEmpty()) {
      leadService.addLead("Ivan", "test1@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Anastasiya", "test2@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Konstantin", "test3@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Nataliya", "test4@example.ru", null, LeadStatus.NEW);
    }

    return "All leads added!";
  }

  // для curl.exe -X POST http://localhost:8081/admin/addTestProducts
  @PostMapping("/addTestProducts")
  @ResponseBody
  public String addTestProducts(Model model) {

    // добавление продуктов
    if (productService.findAll().isEmpty()) {
      productService.addProduct(
          "Консультация по архитектуре", "CONSULT-ARCH-001", new BigDecimal("50000.00"), true);
      productService.addProduct(
          "Консультация по бизнес-аналитике", "CONSULT-BI-001", new BigDecimal("70000.00"), true);
      productService.addProduct("ноутбук DELL", "LAPTOP-001", new BigDecimal("90000.00"), true);
      productService.addProduct(
          "Подписка Microsoft 365 Business", "SOFT-MSFT-001", new BigDecimal("8900.00"), true);
      productService.addProduct(
          "Оптимизация SQL запросов и производительности БД",
          "CONSULT-DB-001",
          new BigDecimal("80000.00"),
          true);
    }

    return "All products added!";
  }

  // для curl.exe -X POST http://localhost:8081/admin/addTestDeal
  @PostMapping("/addTestDeal")
  @ResponseBody
  public String addTestDeal() {

    // добавление сделки с продуктами
    if (dealService.getAllDeals().isEmpty()) {
      // будем создавать сделки с первым и последним лидом
      List<Lead> allLeads = leadService.findAll(); // один раз
      UUID firstLeadId = allLeads.getFirst().getId();
      UUID lastLeadId = allLeads.getLast().getId();

      Deal firstDeal = new Deal(firstLeadId, BigDecimal.valueOf(100_000));
      Deal lastDeal = new Deal(lastLeadId, BigDecimal.valueOf(89_000));

      boolean even = false;
      for (Product p : productService.findAll()) {
        // в одну сделку добавим все продукты
        DealProduct firstDealProduct = new DealProduct(firstDeal, p, 1, p.getPrice());
        firstDeal.addDealProduct(firstDealProduct);

        // в другую сделку добавим каждый второй продукт
        if (even) {
          DealProduct lastDealProduct = new DealProduct(lastDeal, p, 1, p.getPrice());
          lastDeal.addDealProduct(lastDealProduct);
        }
        even = !even;
      }

      dealService.addDeal(firstDeal);
      dealService.addDeal(lastDeal);
    }

    return "All deals added!";
  }
}
