package ru.mentee.power.crm.spring.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/leads")
public class LeadController {
  private final LeadService leadService;

  @GetMapping
  public String showLeads(Model model) {
    List<Lead> leads = leadService.findAll();
    model.addAttribute("leads", leads);
    return "leads/list";
  }
}
