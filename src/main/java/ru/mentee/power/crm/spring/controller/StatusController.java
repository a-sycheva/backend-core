package ru.mentee.power.crm.spring.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.service.StatusService;

@Controller
@RequiredArgsConstructor
public class StatusController {
  private final StatusService service;
  private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);

  @GetMapping("/statuses")
  public String showStatus(
      Model model) {

    List<String> statuses = service.findAll();

    //пока без поиска
    model.addAttribute("statuses", statuses);

    return "status/list";
  }

  //форма создания статуса
  @GetMapping("/statuses/new")
  public String showCreateStatusForm(Model model) {
    model.addAttribute("status", new String());
    return "status/create";
  }

  //создание статуса
  @PostMapping("/statuses")
  public String createStatus(@RequestParam String status) {
    if (service.containsStatus(status)) {
      LOG.info("Попытка добавить дублирующий статус");
    }
    service.addStatus(status);
    return "redirect:/statuses";
  }
}
