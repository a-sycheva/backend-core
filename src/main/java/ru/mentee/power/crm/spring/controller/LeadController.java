package ru.mentee.power.crm.spring.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    return "Spring Boot CRM is running! Leads in Database: " + leadService.findAll().size() + " leads.";
  }

  @GetMapping("/leads")
  public String showLeads(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String company,
      @RequestParam(required = false) LeadStatus status,
      Model model) {
    List<Lead> leads = leadService.findLeads(name, email, company, status);

    model.addAttribute("leads", leads);
    model.addAttribute("email", email);
    model.addAttribute("company", company);
    model.addAttribute("status", status);

    return "leads/list";
  }

  //форма создания лида
  @GetMapping("/leads/new")
  public String showCreateForm(Model model) {
    model.addAttribute("lead", new Lead("", "", "", LeadStatus.NEW));
    return "leads/create";
  }

  //создание лида
  @PostMapping("/leads")
  public String createLead(@Valid @ModelAttribute Lead lead, BindingResult result, Model model) {
    if (result.hasErrors()) {
      model.addAttribute("errors", result);
      return "leads/form";
    } else {
      leadService.addLead(lead.name(), lead.email(), lead.company(), lead.status());
      return "redirect:/leads";
    }
  }

  //форма обновления лида
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
    return "leads/edit";
  }

  //обновление лида
  @PostMapping("/leads/{id}")
  public String updateLead(@PathVariable UUID id, @Valid @ModelAttribute Lead lead,
                           BindingResult result, Model model) {

    //избегаю Direct endpoint invocation
    if (leadService.findById(id).isEmpty()) {

      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Cannot find lead with id " + id);
    } else {

      if (result.hasErrors()) {
        model.addAttribute("errors", result);
        return "leads/form";
      } else {
        leadService.update(id, lead);
        return "redirect:/leads";
      }

    }
  }

  //удаление лида
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


