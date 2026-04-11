package ru.mentee.power.crm.spring.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealProduct;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.model.Product;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.ProductJpaRepository;
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
        + "Total leads: " + leadService.findAll().size();
  }

  // для curl.exe -X POST http://localhost:8081/admin/addTestData
  @PostMapping("/addTestData")
  @ResponseBody
  public String addTestData(Model model) {
    //добавление лидов
    if (leadService.findAll().isEmpty()) {
      leadService.addLead("Ivan", "test1@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Anastasiya", "test2@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Konstantin", "test3@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Nataliya", "test4@example.ru", null, LeadStatus.NEW);
    }

    //добавление продуктов
    if (productService.findAll().isEmpty()) {
      productService.addProduct("Консультация по архитектуре", "CONSULT-ARCH-001",
          new BigDecimal("50000.00"), true );
      productService.addProduct("Консультация по бизнес-аналитике", "CONSULT-BI-001",
          new BigDecimal("70000.00"), true);
      productService.addProduct("ноутбук DELL", "LAPTOP-001",
          new BigDecimal("90000.00"), true);
      productService.addProduct("Подписка Microsoft 365 Business", "SOFT-MSFT-001",
          new BigDecimal("8900.00"), true);
      productService.addProduct("Оптимизация SQL запросов и производительности БД", "CONSULT-DB-001",
          new BigDecimal("80000.00"), true);
    }

    //добавление сделки с продуктами
    if (dealService.getAllDeals().isEmpty()) {
      //будем создавать сделки с первым попавшимся лидом
      UUID leadId = leadService.findAll().getFirst().getId();
      Deal deal = new Deal(leadId, BigDecimal.valueOf(100_000));

      for(Product p : productService.findAll() ) {
        DealProduct dealProduct = new DealProduct(deal, p, 1, p.getPrice());
        deal.addDealProduct(dealProduct);
      }

      dealService.addDeal(deal);
    }

    return "All data added!";
  }
}
