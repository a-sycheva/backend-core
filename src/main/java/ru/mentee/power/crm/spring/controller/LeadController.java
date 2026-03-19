package ru.mentee.power.crm.spring.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequiredArgsConstructor
public class LeadController {
  private final LeadService leadService;

  @GetMapping
  @ResponseBody
  public String home() {
    return "Spring Boot CRM is running! Beans created: " + leadService.findAll().size() + " leads.";
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

  @GetMapping("/leads/new")
  public String showCreateForm(Model model) {
    model.addAttribute("lead", new Lead(null, "", "", LeadStatus.NEW));
    return "leads/create";
  }

  @GetMapping("/leads/{id}/edit")
  public String showEditForm(@PathVariable UUID id, Model model) {

    Optional<Lead> lead = leadService.findById(id);
    if (lead.isEmpty()) {

      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Cannot find lead with id " + id);
    } else {
      model.addAttribute("lead", lead.get());
    }
    return "spring/edit";
  }

  @PostMapping("/leads/{id}")
  public String updateLead(@PathVariable UUID id, @ModelAttribute Lead lead) {
    leadService.update(id, lead);
    return "redirect:/leads";
  }

  @PostMapping("/leads/{id}/delete")
  public String deleteLead(@PathVariable UUID id) {
    if (leadService.findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Lead with id = " + id + " not exists");
    } else {
      leadService.delete(id);
      return "redirect:/leads";
    }
  }

}


