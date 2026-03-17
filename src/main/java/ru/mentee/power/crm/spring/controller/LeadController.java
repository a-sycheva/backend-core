package ru.mentee.power.crm.spring.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequiredArgsConstructor
//@RequestMapping("/leads")
public class LeadController {
  private final LeadService leadService;

  @GetMapping
  @ResponseBody
  public String home() {
    return "Spring Boot CRM is running! Beans created: " + leadService.findAll().size() + " leads.";
  }

  @GetMapping("/leads/new")
  public String showCreateForm(Model model) {
    model.addAttribute("lead", new Lead(null, "", "", LeadStatus.NEW));
    return "leads/create";
  }

  @GetMapping("/leads")
  public String showLeads(
      @RequestParam(required = false) LeadStatus status, Model model) {
    List<Lead> leads = (status == null)
        ? leadService.findAll()
        : leadService.findByStatus(status);

    model.addAttribute("leads", leads);
    model.addAttribute("currentFilter", status);
    return "leads/list";
  }

  @PostMapping("/leads")
  public String createLead(@ModelAttribute Lead lead) {
    leadService.addLead(lead.email(), lead.company(), lead.status());
    return "redirect:/leads";
  }
}


