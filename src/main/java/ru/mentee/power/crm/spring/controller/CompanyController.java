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
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.service.CompanyService;

@Controller
@RequiredArgsConstructor
public class CompanyController {
  private  final CompanyService companyService;

  @GetMapping("/companies")
  public String showCompanies(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String industry,
      Model model) {
    List<Company> companies = companyService.findCompanies(name, industry);

    model.addAttribute("companies", companies);
    model.addAttribute("name", name);
    model.addAttribute("industry", industry);

    return "companies/list";
  }

  //форма создания компании
  @GetMapping("/companies/new")
  public String showCreateForm(Model model) {
    model.addAttribute("company", new Company("", ""));
    return "companies/create";
  }

  //создание компании
  @PostMapping("/companies")
  public String createCompanies (@Valid @ModelAttribute Company company,
                           BindingResult result,
                           Model model) {
    if (result.hasErrors()) {
      model.addAttribute("errors", result);
      return "companies/form";
    } else {

      companyService.addCompany(company.getName(), company.getIndustry());
      return "redirect:/companies";
    }
  }

  //форма обновления компании
  @GetMapping("/companies/{id}/edit")
  public String showEditForm(@PathVariable UUID id, Model model) {

    Optional<Company> company = companyService.findById(id);
    if (company.isEmpty()) {

      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Cannot find company with id " + id);
    } else {
      model.addAttribute("company", company.get());
    }
    return "companies/edit";
  }

  //обновление лида
  @PostMapping("/companies/{id}")
  public String updateCompany(@PathVariable UUID id,
                           @Valid @ModelAttribute Company company,
                           BindingResult result,
                           Model model) {

    //избегаю Direct endpoint invocation
    if (companyService.findById(id).isEmpty()) {

      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Cannot find company with id " + id);
    } else {
      if (result.hasErrors()) {
        model.addAttribute("errors", result);
        return "companies/form";
      } else {
        companyService.update(id, company);
        return "redirect:/companies";
      }

    }
  }

  //удаление лида
  @PostMapping("/companies/{id}/delete")
  public String deleteCompany(@PathVariable UUID id) {
    if (companyService.findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Company with id = " + id + " not exists");
    } else {
      companyService.delete(id);
      return "redirect:/companies";
    }
  }

}
