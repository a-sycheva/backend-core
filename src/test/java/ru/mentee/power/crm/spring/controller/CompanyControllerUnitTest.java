package ru.mentee.power.crm.spring.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.service.CompanyService;
import ru.mentee.power.crm.service.LeadService;

@WebMvcTest(CompanyController.class)
public class CompanyControllerUnitTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CompanyService companyService;

  @MockitoBean
  private  LeadService leadService;

  @Test
  void shouldReturnCompaniesWhenFilteredByName() throws Exception {
    Company company = new Company("TestCorp", "TestIndustry");
    List<Company> companies = new ArrayList<>();
    companies.add(company);

    when(companyService.findCompanies("TestCorp", null))
        .thenReturn(companies);

    mockMvc.perform(get("/companies")
            .param("name", "TestCorp"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().attribute("companies", companies))
        .andExpect(model().attribute("name", "TestCorp"));
  }

  @Test
  void shouldReturnCompaniesWhenFilteredByIndustry() throws Exception {
    Company company = new Company("TestCorp", "TestIndustry");
    List<Company> companies = new ArrayList<>();
    companies.add(company);

    when(companyService.findCompanies(null, "TestIndustry"))
        .thenReturn(companies);

    mockMvc.perform(get("/companies")
            .param("industry", "TestIndustry"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().attribute("companies", companies))
        .andExpect(model().attribute("industry", "TestIndustry"));
  }

  @Test
  void shouldShowCreateForm() throws Exception {
    mockMvc.perform(get("/companies/new"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("company"))
        .andExpect(view().name("companies/create"));
  }

  @Test
  void shouldCreateCompanyWhenValid() throws Exception {
    mockMvc.perform(post("/companies")
            .param("name", "New Corp")
            .param("industry", "IT"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/companies"));

    verify(companyService).addCompany("New Corp", "IT");
  }

  @Test
  void shouldShowEditFormWhenCompanyExists() throws Exception {
    UUID id = UUID.randomUUID();
    Company company = new Company("TestCorp", "TestIndustry");
    when(companyService.findById(id)).thenReturn(Optional.of(company));

    mockMvc.perform(get("/companies/{id}/edit", id))
        .andExpect(status().isOk())
        .andExpect(model().attribute("company", company))
        .andExpect(view().name("companies/edit"));

    verify(companyService).findById(id);
  }

  @Test
  void shouldReturn404WhenEditNonExistentCompany() throws Exception {
    UUID id = UUID.randomUUID();
    when(companyService.findById(id)).thenReturn(Optional.empty());

    mockMvc.perform(get("/companies/{id}/edit", id))
        .andExpect(status().isNotFound());

    verify(companyService).findById(id);
  }

  @Test
  void shouldUpdateCompanyWhenValid() throws Exception {
    UUID id = UUID.randomUUID();
    Company existingCompany = new Company("Old Corp", "OldIndustry");
    Company updatedCompany = new Company("Updated Corp", "UpdatedIndustry");
    when(companyService.findById(id)).thenReturn(Optional.of(existingCompany));
    when(companyService.update(eq(id), any(Company.class))).thenReturn(updatedCompany);

    mockMvc.perform(post("/companies/{id}", id)
            .param("name", "Updated Corp")
            .param("industry", "UpdatedIndustry"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/companies"));

    verify(companyService).update(eq(id), any(Company.class));
  }

  @Test
  void shouldReturn404WhenUpdateNonExistentCompany() throws Exception {
    UUID id = UUID.randomUUID();
    when(companyService.findById(id)).thenReturn(Optional.empty());

    mockMvc.perform(post("/companies/{id}", id)
            .param("name", "New Corp")
            .param("industry", "IT"))
        .andExpect(status().isNotFound());

    verify(companyService, never()).update(eq(id), any(Company.class));
  }

  @Test
  void shouldThrowExceptionWhenDeleteNotExistedCompany() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(post("/companies/{id}/delete", id))
        .andExpect(status().is4xxClientError());

    verify(companyService).findById(id);
  }

  @Test
  void shouldDeleteCompanyAndRedirect() throws Exception {
    Company company = new Company("TestCorp", "TestIndustry");
    UUID id = UUID.randomUUID();

    when(companyService.findById(id)).thenReturn(Optional.of(company));
    doNothing().when(companyService).delete(id);

    mockMvc.perform(post("/companies/{id}/delete", id))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/companies"));

    verify(companyService).delete(id);
  }
}
