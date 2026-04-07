package ru.mentee.power.crm.spring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final LeadService leadService;

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
    if (leadService.findAll().isEmpty()) {
      leadService.addLead("Ivan", "test1@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Anastasiya", "test2@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Konstantin", "test3@example.ru", null, LeadStatus.NEW);
      leadService.addLead("Nataliya", "test4@example.ru", null, LeadStatus.NEW);
      return "Data is added!";
    }
    return "Database already has data!";
  }
}
